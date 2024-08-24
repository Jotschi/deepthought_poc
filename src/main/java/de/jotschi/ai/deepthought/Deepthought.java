package de.jotschi.ai.deepthought;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.jotschi.ai.deepthought.cache.JsonCache;
import de.jotschi.ai.deepthought.datasource.DatasourceManager;
import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.llm.LLMContext;
import de.jotschi.ai.deepthought.llm.ollama.OllamaService;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.model.Thought;
import de.jotschi.ai.deepthought.model.memory.DecompositionResult;
import de.jotschi.ai.deepthought.model.memory.DecompositionStep;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class Deepthought {

    private static final LLM PRIMARY_LLM = LLM.OLLAMA_GEMMA2_27B_INST_Q8;
    private static final int MAX_DEPTH = 3;
    private boolean RECURSIVE = false;

    private OllamaService llm;
    private PromptService ps;
    private static ObjectMapper mapper = new ObjectMapper();
    private JsonCache cache = new JsonCache();
    private DatasourceManager dsm = new DatasourceManager();

    private Map<String, List<String>> mockContextMap = new HashMap<>();

    public Deepthought(OllamaService llm, PromptService ps) {
        this.llm = llm;
        this.ps = ps;
    }

    private JsonObject processQuery(String query, String context) {

        Prompt prompt = null;
        if (context != null) {
            prompt = ps.getPrompt(PromptKey.DECOMPOSE_WITH_CONTEXT);
            prompt.set("context", context);
        } else {
            prompt = ps.getPrompt(PromptKey.DECOMPOSE);
        }

        LLMContext ctx = LLMContext.ctx(prompt, PRIMARY_LLM);
        ctx.setText(query);
//        llm.listModels(ctx);
        String out = llm.generate(ctx);
        System.out.println(out);
        JsonArray json = new JsonArray(out);
        JsonArray jsonOut = new JsonArray();
        for (int i = 0; i < json.size(); i++) {
            // In
            JsonObject stepIn = json.getJsonObject(i);
            boolean queryFlag = stepIn.getBoolean("wissensabfrage");
            String queryText = stepIn.getString("wissensabfrage_query");
            String queryType = stepIn.getString("wissensabfrage_typ");
            String text = stepIn.getString("anweisung");
            String expert = stepIn.getString("experte");
            boolean processable = stepIn.getBoolean("zerlegbar");
            int relevance = stepIn.getInteger("relevanz");
            if (queryFlag && (queryText == null || queryType == null)) {
                System.out.println(stepIn.encodePrettily());
                throw new RuntimeException("Invalid json - invalid query");
            }
            if (text == null) {
                throw new RuntimeException("Invalid json - no text");
            }
            if (relevance < 0 || relevance > 10) {
                throw new RuntimeException("Invalid json - invalid relevance range");
            }

            System.out.println(stepIn.encodePrettily());

            // Out
            JsonObject stepOut = new JsonObject();
            stepOut.put("query_flag", queryFlag);
            if (queryFlag) {
                stepOut.put("query_text", queryText);
                stepOut.put("query_type", queryType);
            } else {
                stepOut.put("query_text", null);
                stepOut.put("query_type", null);
            }
            stepOut.put("processable", processable);
            stepOut.put("relevance", relevance);
            stepOut.put("text", text);
            stepOut.put("expert", expert);
            jsonOut.add(stepOut);
        }

        JsonObject datasetEntry = new JsonObject();
        datasetEntry.put("query", query);
        datasetEntry.put("steps", jsonOut);
        System.out.println(datasetEntry.encodePrettily());
        return datasetEntry;
    }

    private String quote(String text) {
        return text.replaceAll(".*\\R|.+\\z", Matcher.quoteReplacement("> ") + "$0");
    }

    public DatasourceManager datasourceManager() {
        return dsm;
    }

    public Thought process(String query) throws IOException {
        Thought root = Thought.of(query);
        // Load an initial set chunks that might be relevant to the query
        List<String> contextList = lookupQueryContext(query);

        // Add the initial root entries
        for (String context : contextList) {
            root.add(Thought.of(query, context));
        }

        // Start decompose process for each branch
        for (Thought thought : root.thoughts()) {
            decompose(thought);
        }

        // Now evaluate the branches
        for (Thought t2 : root.thoughts()) {
            for (Thought t3 : t2.thoughts()) {
                evaluateThought(t3);
            }
        }

        return root;
    }

    private void evaluateThought(Thought t) {
        JsonObject json = cache.computeIfAbsent("eval", t.id(), cid -> {
            return eval(t.text(), t.context(), t.expert());
        });
        t.setResult(json.getString("text"));

        for (Thought thought : t.thoughts()) {
            evaluateThought(thought);
        }
    }

    private JsonObject eval(String query, String context, String expert) {
        Prompt prompt = null;
        if (context == null) {
            prompt = ps.getPrompt(PromptKey.STEP);
        } else {
            prompt = ps.getPrompt(PromptKey.STEP_WITH_CONTEXT);
            prompt.set("context", context);
        }

        prompt.set("expert", expert);
        LLMContext ctx = LLMContext.ctx(prompt, PRIMARY_LLM);
        ctx.setText(query);
        String text = llm.generate(ctx);
        return new JsonObject().put("text", text);
    }

    private void decompose(Thought thought) throws JsonMappingException, JsonProcessingException {
        JsonObject json = cache.computeIfAbsent("decomp", thought.id(), cid -> {
            return processQuery(thought.text(), thought.context());
        });
        DecompositionResult decomp = mapper.readValue(json.encodePrettily(), DecompositionResult.class);

        for (DecompositionStep step : decomp.getSteps()) {
            String stepQuery = step.isQueryFlag() ? step.getQueryText() : step.getText();
            Thought stepThought = Thought.of(stepQuery).setExpert(step.getExpert());
            thought.add(stepThought);
            if (RECURSIVE) {
                decompose(stepThought);
            }
        }
    }

    public String answer(Thought t) {

        // Start with the root elements and iterate over all thoughts
        // TODO only eval the best branch
        int i = 0;
        StringBuilder builder = new StringBuilder();
        for (Thought thought : t.thoughts()) {
            StringBuilder contextBuilder = new StringBuilder();
            Prompt prompt = ps.getPrompt(PromptKey.FINALIZE);
            LLMContext ctx = LLMContext.ctx(prompt, PRIMARY_LLM);
            for (Thought subThought : thought.thoughts()) {
                contextBuilder.append("\n\n# " + subThought.text() + ":\n\n" + quote(subThought.result()));
            }
            prompt.set("feedback", contextBuilder.toString());
            ctx.setText(thought.text());
            builder.append("[" + (i++) + "] => " + llm.generate(ctx) + "\n\n");
        }
        return builder.toString();
    }

    protected List<String> lookupQueryContext(String query) {
        return mockContextMap.get(query);
    }

    public void addMockQueryContext(String query, List<String> contextList) {
        this.mockContextMap.put(query, contextList);
    }

}
