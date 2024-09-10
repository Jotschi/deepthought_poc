package de.jotschi.ai.deepthought.ops.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.jotschi.ai.deepthought.Deepthought;
import de.jotschi.ai.deepthought.llm.LLMContext;
import de.jotschi.ai.deepthought.llm.ollama.CachingAsyncOllamaService;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.model.Thought;
import de.jotschi.ai.deepthought.model.memory.DecompositionResult;
import de.jotschi.ai.deepthought.model.memory.DecompositionStep;
import de.jotschi.ai.deepthought.ops.AbstractDeepthoughtOperation;
import de.jotschi.ai.deepthought.ops.EvalHelper;
import de.jotschi.ai.deepthought.util.TextUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class DeepthoughtDecomposeOperation extends AbstractDeepthoughtOperation {

    private DeepthoughtDecomposeContextOperation contextOp;

    public DeepthoughtDecomposeOperation(CachingAsyncOllamaService llm, PromptService ps) {
        super(llm, ps);
        this.contextOp = new DeepthoughtDecomposeContextOperation(llm, ps);
    }

    public void process(Thought thought) throws JsonMappingException, JsonProcessingException {
        JsonObject json = computeDecompose(thought.text(), thought.context());
        DecompositionResult decomp = mapper.readValue(json.encodePrettily(), DecompositionResult.class);

        for (DecompositionStep step : decomp.getSteps()) {
            String stepQuery = step.isQueryFlag() ? step.getQueryText() : step.getText();
            String context = step.getContext();
            Thought stepThought = Thought.of(stepQuery, context).setExpert(step.getExpert());
            thought.add(stepThought);
            if (Deepthought.RECURSIVE) {
                process(stepThought);
            }
        }
        thought.setSummaryQuery(decomp.getSummaryQuery());
    }

    private JsonObject computeDecompose(String query, String context) {

        Prompt prompt = null;
        if (context != null) {
            prompt = ps.getPrompt(PromptKey.DECOMPOSE_WITH_CONTEXT);
            prompt.set("context", context);
        } else {
            prompt = ps.getPrompt(PromptKey.DECOMPOSE);
        }

        LLMContext ctx = LLMContext.ctx(prompt, Deepthought.PRIMARY_LLM);
        prompt.setText(TextUtil.quote(query));

        // System.out.println(prompt.llmInput());
        JsonObject json = llm.generateJson(prompt, Deepthought.PRIMARY_LLM).get();
        JsonObject jsonOut = new JsonObject();
        // System.out.println(out);

        String summaryQuery = json.getString("schlussgedanke");
        JsonArray schritte = json.getJsonArray("gedanken");
        for (int i = 0; i < schritte.size(); i++) {
            // In
            JsonObject stepIn = schritte.getJsonObject(i);
            boolean queryFlag = stepIn.getBoolean("wissensabfrage");
            String queryText = stepIn.getString("wissensabfrage_query");
            String queryType = stepIn.getString("wissensabfrage_typ");
            String text = stepIn.getString("gedanke");
            String stepContext = EvalHelper.evaluate(llm, query, q -> contextOp.process(text, q));
            String expert = stepIn.getString("experte");
            boolean processable = stepIn.getBoolean("zerlegbar");
            int relevance = stepIn.getInteger("relevanz");
            if (queryFlag && (queryText == null || queryType == null)) {
                // System.out.println(stepIn.encodePrettily());
                throw new RuntimeException("Invalid json - invalid query");
            }
            if (text == null) {
                throw new RuntimeException("Invalid json - no text");
            }
            if (relevance < 0 || relevance > 10) {
                throw new RuntimeException("Invalid json - invalid relevance range");
            }

            // System.out.println(stepIn.encodePrettily());

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
            stepOut.put("context", stepContext);
            jsonOut.add(stepOut);
        }

        JsonObject datasetEntry = new JsonObject();
        datasetEntry.put("query", query);
        datasetEntry.put("steps", jsonOut);
        datasetEntry.put("summary_query", summaryQuery);
        datasetEntry.put("context", context);
        // System.out.println(datasetEntry.encodePrettily());
        return datasetEntry;
    }

}
