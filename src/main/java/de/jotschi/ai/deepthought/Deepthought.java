package de.jotschi.ai.deepthought;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.jotschi.ai.deepthought.cache.JsonCache;
import de.jotschi.ai.deepthought.datasource.DatasourceManager;
import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.llm.LLMContext;
import de.jotschi.ai.deepthought.llm.ollama.OllamaService;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.model.DecompositionStep;
import de.jotschi.ai.deepthought.model.DeepthoughtMemory;
import de.jotschi.ai.deepthought.model.DeepthoughtMemoryEntry;
import de.jotschi.ai.deepthought.model.QueryContext;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class Deepthought {

    private static final LLM PRIMARY_LLM = LLM.OLLAMA_GEMMA2_27B_INST_Q8;
    private static final int MAX_DEPTH = 3;
    private boolean RECURSIVE = false;

    private OllamaService llm;
    private PromptService ps;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonCache cache = new JsonCache();
    private DatasourceManager dsm = new DatasourceManager();

    private List<QueryContext> mockQueryContextList;

    public Deepthought(OllamaService llm, PromptService ps) {
        this.llm = llm;
        this.ps = ps;
    }

    public DeepthoughtMemoryEntry decompose(String query, QueryContext context) throws IOException {
        JsonObject json = cache.computeIfAbsent("decomp", query + context, cid -> {
            return processQuery(query, context);
        });
        DeepthoughtMemoryEntry decomp = mapper.readValue(json.encodePrettily(), DeepthoughtMemoryEntry.class);
        if (RECURSIVE) {
            return decompose(1, decomp);
        } else {
            return decomp;
        }
    }

    private DeepthoughtMemoryEntry decompose(int level, DeepthoughtMemoryEntry decomposition) throws IOException {
        int n = 1;
        if (level >= MAX_DEPTH) {
            return decomposition;
        }
        // Process each step individually
        for (DecompositionStep step : decomposition.steps()) {
            if (step.isProcessable()) {
                System.out.println("Decomposing: [" + level + "." + n + "]");
                String query = step.isQueryFlag() ? step.getQueryText() : step.getText();
                System.out.println(query);
                JsonObject subJson = cache.computeIfAbsent("decomp", query, cid -> {
                    return processQuery(query, decomposition.context());
                });
                DeepthoughtMemoryEntry decomp = mapper.readValue(subJson.encodePrettily(), DeepthoughtMemoryEntry.class);
                step.setEntry(decomp);
                decompose(level + 1, decomp);
                n++;
            }
        }
        return decomposition;
    }

    public String evalStep(int id, DecompositionStep step, QueryContext queryCtx) throws IOException {
        String query = step.isQueryFlag() ? step.getQueryText() : step.getText();
        JsonObject json = cache.computeIfAbsent("eval", query + "_" + id, cid -> {
            // Check whether we need to query another source
//                return dsm.process(step);
            return eval(step, queryCtx, query);
        });
        return json.getString("text");
    }

    private JsonObject eval(DecompositionStep step, QueryContext queryCtx, String query) {
        Prompt prompt = null;
        if (queryCtx == null) {
            prompt = ps.getPrompt(PromptKey.STEP);
        } else {
            prompt = ps.getPrompt(PromptKey.STEP_WITH_CONTEXT);
            prompt.set("context", queryCtx.getText());
        }

        prompt.set("expert", step.getExpert());
        LLMContext ctx = LLMContext.ctx(prompt, PRIMARY_LLM);
        ctx.setText(query);
        String text = llm.generate(ctx);
        return new JsonObject().put("text", text);
    }

    private JsonObject processQuery(String query, QueryContext context) {

        Prompt prompt = null;
        if (context != null) {
            prompt = ps.getPrompt(PromptKey.DECOMPOSE_WITH_CONTEXT);
            prompt.set("context", context.getText());
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

    private String answer(DeepthoughtMemory memory) {

        // Start with the root elements
        for (DeepthoughtMemoryEntry entry : memory.entries()) {
            StringBuilder builder = new StringBuilder();
            String ctxText = entry.result();

            // TODO only eval the best branch
            Prompt prompt = ps.getPrompt(PromptKey.FINALIZE);
            LLMContext ctx = LLMContext.ctx(prompt, PRIMARY_LLM);
            List<DeepthoughtMemoryEntry> entries = memory.entries();
            System.out.println("Using: " + entries.size() + " memory entries");

            for (DecompositionStep step : entry.steps()) {

                builder.append("\n\n# " + step.getText() + ":\n\n" + quote(step.getResult()));
            }
            prompt.set("feedback", builder.toString());
            ctx.setText(memory.query());
            String stepOut = llm.generate(ctx);
            System.out.println(stepOut);
            return stepOut;
        }
        return null;

    }

    private String quote(String text) {
        return text.replaceAll(".*\\R|.+\\z", Matcher.quoteReplacement("> ") + "$0");
    }

    public DatasourceManager datasourceManager() {
        return dsm;
    }

    public String process(String query) throws IOException {
        // Initialize the state for processing
        DeepthoughtMemory memory = new DeepthoughtMemory(this, query);

        // Load an initial set chunks that might be relevant to the query
        List<QueryContext> contextList = lookupQueryContext(query);

        // Add the initial root entries to the memory
        for (QueryContext context : contextList) {
            memory.entries().add(new DeepthoughtMemoryEntry(query, context, null, null));
        }

        // Decompose and eval the memory entries
        memory.decompose();

        return answer(memory);
    }

    private List<QueryContext> lookupQueryContext(String query) {
        return mockQueryContextList;
    }

    public void setMockQueryContextList(List<QueryContext> mockQueryContextList) {
        this.mockQueryContextList = mockQueryContextList;
    }

}
