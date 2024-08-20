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
import de.jotschi.ai.deepthought.model.Decomposition;
import de.jotschi.ai.deepthought.model.DecompositionStep;
import de.jotschi.ai.deepthought.model.DeepthoughtMemory;
import de.jotschi.ai.deepthought.model.MemorizedThought;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class Deepthought {

    private static final LLM PRIMARY_LLM = LLM.OLLAMA_MISTRAL_7B_INST_V03_FP16;

    private OllamaService llm;
    private PromptService ps;
    private ObjectMapper mapper = new ObjectMapper();
    private DeepthoughtMemory memory = new DeepthoughtMemory(this);
    private JsonCache cache = new JsonCache();
    private DatasourceManager dsm = new DatasourceManager();

    public Deepthought(OllamaService llm, PromptService ps) {
        this.llm = llm;
        this.ps = ps;
    }

    public Decomposition decompose(String query) throws IOException {
        JsonObject json = cache.computeIfAbsent("decomp", query, cid -> {
            return processQuery(query);
        });
        Decomposition decomp = mapper.readValue(json.encodePrettily(), Decomposition.class);
        return decomp;
    }

    public String evalStep(int id, String query, DecompositionStep step) throws IOException {
        JsonObject json = cache.computeIfAbsent("eval", query + "_" + id, cid -> {
            // Check whether we need to query another source
            if (step.isQueryFlag()) {
//                return dsm.process(step);
                
                Prompt prompt = ps.getPrompt(PromptKey.STEP);
                prompt.set("expert", step.getExpert());
                LLMContext ctx = LLMContext.ctx(prompt, PRIMARY_LLM);
                ctx.setText(step.getQueryText());
                String text = llm.generate(ctx);
                return new JsonObject().put("text", text);
            } else {
                Prompt prompt = ps.getPrompt(PromptKey.STEP);
                prompt.set("expert", step.getExpert());
                LLMContext ctx = LLMContext.ctx(prompt, PRIMARY_LLM);
                ctx.setText(step.getText());
                String text = llm.generate(ctx);
                return new JsonObject().put("text", text);
            }
        });
        return json.getString("text");
    }

    private JsonObject processQuery(String query) {
        Prompt prompt = ps.getPrompt(PromptKey.DECOMPOSE);

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

    public DeepthoughtMemory memory() {
        return memory;
    }

    public String answer() {
        Prompt prompt = ps.getPrompt(PromptKey.FINALIZE);
        LLMContext ctx = LLMContext.ctx(prompt, PRIMARY_LLM);
        StringBuilder builder = new StringBuilder();
        List<MemorizedThought> thoughts = memory.thoughts();
        System.out.println("Using: " + thoughts.size() + " thoughts");
        for (MemorizedThought thought : thoughts) {
            String ctxText = thought.result();
            builder.append("\n\n# " + thought.step().getText() + ":\n\n" + quote(ctxText));
        }

        prompt.set("feedback", builder.toString());
        ctx.setText(memory.query());
        String stepOut = llm.generate(ctx);
        System.out.println(stepOut);
        return stepOut;

    }

    private String quote(String text) {
        return text.replaceAll(".*\\R|.+\\z", Matcher.quoteReplacement("> ") + "$0");
    }

    public DatasourceManager datasourceManager() {
        return dsm;
    }

}
