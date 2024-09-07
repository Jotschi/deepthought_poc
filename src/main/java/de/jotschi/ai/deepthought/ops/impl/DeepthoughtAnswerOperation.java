package de.jotschi.ai.deepthought.ops.impl;

import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.jotschi.ai.deepthought.Deepthought;
import de.jotschi.ai.deepthought.llm.LLMContext;
import de.jotschi.ai.deepthought.llm.ollama.OllamaService;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.model.Thought;
import de.jotschi.ai.deepthought.ops.AbstractDeepthoughtOperation;
import de.jotschi.ai.deepthought.util.HashUtil;
import de.jotschi.ai.deepthought.util.TextUtil;
import io.vertx.core.json.JsonObject;

public class DeepthoughtAnswerOperation extends AbstractDeepthoughtOperation {

    public DeepthoughtAnswerOperation(OllamaService llm, PromptService ps) {
        super(llm, ps);
    }

    @Override
    public void process(Thought thought) throws JsonMappingException, JsonProcessingException {
        // TODO Auto-generated method stub

    }

    public JsonObject answer(String query, String context, String expert) {
        Prompt prompt = null;
        if (context == null) {
            prompt = ps.getPrompt(PromptKey.ANSWER);
        } else {
            prompt = ps.getPrompt(PromptKey.ANSWER_WITH_CONTEXT);
            prompt.set("context", context);
        }

        if (expert != null) {
            prompt.set("expert", expert);
        }
        prompt.setText(query);
        LLMContext ctx = LLMContext.ctx(prompt, Deepthought.PRIMARY_LLM);
        String jsonStr = llm.generate(ctx, "json");
        JsonObject json = new JsonObject(jsonStr);
        json.put("query", query);
        json.put("context", context);
        json.put("expert", expert);
        System.out.println(json.encodePrettily());
        return json;
    }

    public void answerThought(Thought t) {
        JsonObject json = cache.computeIfAbsent("answer", t.id(), cid -> {
            return answer(t.text(), t.context(), t.expert());
        });
        t.setResult(json.getString("antwort"));
        t.setConfidence(parseAnteil(json.getString("anteil")));

        for (Thought thought : t.thoughts()) {
            answerThought(thought);
        }
    }

    private int parseAnteil(String anteilStr) {
        anteilStr = anteilStr.trim().replaceAll("%", "");
        anteilStr = anteilStr.replaceAll(",", ".");
        if (anteilStr.contains(".")) {
            return (int) Float.parseFloat(anteilStr);
        }
        return Integer.parseInt(anteilStr);
    }
    
    public String computeAnswer(Thought t) throws NoSuchAlgorithmException {

        // Start with the root elements and iterate over all thoughts
        // TODO only eval the best branch
        int i = 0;
        StringBuilder builder = new StringBuilder();
        for (Thought thought : t.thoughts()) {
            StringBuilder contextBuilder = new StringBuilder();

            Prompt prompt = ps.getPrompt(PromptKey.FINALIZE);
            LLMContext ctx = LLMContext.ctx(prompt, Deepthought.PRIMARY_LLM);
            for (Thought subThought : thought.thoughts()) {
                contextBuilder.append("\n\n# " + subThought.text() + ":\n\n" + TextUtil.quote(subThought.result()));
            }
            prompt.set("feedback", contextBuilder.toString());
            prompt.setText(thought.text());
            String cacheKeyValue = HashUtil.md5(PromptKey.FINALIZE.name() + "_" + Deepthought.PRIMARY_LLM.key() + "_" + thought.text() + "_" + contextBuilder.toString());

            JsonObject json = cache.computeIfAbsent("final", cacheKeyValue, cid -> {
                String txt = llm.generate(ctx, "text");
                return new JsonObject().put("text", txt);
            });
            int score = thought.score();
            builder.append("[" + (i++) + "|" + score + "] => " + json.getString("text") + "\n");
        }
        return builder.toString();
    }

}
