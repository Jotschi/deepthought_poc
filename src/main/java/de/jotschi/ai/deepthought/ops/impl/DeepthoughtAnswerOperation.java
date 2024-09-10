package de.jotschi.ai.deepthought.ops.impl;

import de.jotschi.ai.deepthought.Deepthought;
import de.jotschi.ai.deepthought.llm.ollama.CachingAsyncOllamaService;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.model.Thought;
import de.jotschi.ai.deepthought.ops.AbstractDeepthoughtOperation;
import de.jotschi.ai.deepthought.ops.EvalHelper;
import de.jotschi.ai.deepthought.util.TextUtil;
import io.vertx.core.json.JsonObject;

public class DeepthoughtAnswerOperation extends AbstractDeepthoughtOperation {

    public DeepthoughtAnswerOperation(CachingAsyncOllamaService llm, PromptService ps) {
        super(llm, ps);
    }

    /**
     * Answer t using the result of prev
     * 
     * @param t
     * @param prev
     */
    public void answerThought(Thought t, Thought prev) {
        JsonObject json = cache.computeIfAbsent("answer", t.id(), cid -> {
            return answer(t, prev);
        });
        //System.out.println(json.encodePrettily());
        t.setResult(json.getString("antwort"));
        t.setConfidence(parseAnteil(json.getString("anteil")));
    }

    public JsonObject answer(Thought t, Thought prev) {

        String text = t.text();
        String context = t.context();
        String expert = t.expert();

        Prompt prompt = null;
        if (context == null) {
            prompt = ps.getPrompt(PromptKey.ANSWER);
            prompt.set("context", "");
        } else {
            prompt = ps.getPrompt(PromptKey.ANSWER_WITH_CONTEXT);
            prompt.set("context", "Kontext:\n" + context);
        }

        if (expert != null) {
            prompt.set("expert", expert);
        }
        if (prev != null) {
            StringBuilder prevContext = new StringBuilder();
            prevContext.append("Diese weiteren Informationen stehen dir bereit:");
            prevContext.append("# " + prev.text() + "\n");
            prevContext.append(TextUtil.quote(prev.result()));
            prompt.set("prev_context", prevContext.toString());
        } else {
            prompt.set("prev_context", "");
        }

        prompt.set("query", text);

        String jsonStr = EvalHelper.evaluate(llm, text, q -> {
            return llm.generateJson(prompt, Deepthought.PRIMARY_LLM).get();
        });
        JsonObject json = new JsonObject(jsonStr);
        // json.put("query", query);
        json.put("context", context);
        json.put("expert", expert);
        json.put("prompt", prompt.llmInput());
        //System.out.println(json.encodePrettily());
        return json;
    }

    private int parseAnteil(String anteilStr) {
        anteilStr = anteilStr.trim().replaceAll("%", "");
        anteilStr = anteilStr.replaceAll(",", ".");
        if (anteilStr.contains(".")) {
            return (int) Float.parseFloat(anteilStr);
        }
        return Integer.parseInt(anteilStr);
    }

}
