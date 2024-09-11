package de.jotschi.ai.deepthought.ops.impl;

import java.util.concurrent.ExecutionException;

import de.jotschi.ai.deepthought.Deepthought;
import de.jotschi.ai.deepthought.llm.ollama.CachingAsyncOllamaService;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.model.Thought;
import de.jotschi.ai.deepthought.ops.AbstractDeepthoughtOperation;
import de.jotschi.ai.deepthought.util.TextUtil;
import io.vertx.core.json.JsonObject;

public class DeepthoughtEvaluateOperation extends AbstractDeepthoughtOperation {

    public DeepthoughtEvaluateOperation(CachingAsyncOllamaService llm, PromptService ps) {
        super(llm, ps);
    }

    public JsonObject evaluate(Thought t) throws InterruptedException, ExecutionException {
        Prompt prompt = null;
        prompt = ps.getPrompt(PromptKey.EVAL);

        String expert = t.expert();
        if (expert != null) {
            prompt.set("expert", expert);
        }
        prompt.set("query", t.text());
        prompt.set("result", t.result());

        StringBuilder builder = new StringBuilder();
        for (Thought sub : t.parent().thoughts()) {
            if (sub.id().equals(t.id())) {
                continue;
            }
            builder.append("# " + sub.text() + ":\n" + TextUtil.quote(sub.result()) + "\n\n");
        }
        prompt.set("extra", builder.toString());
        //System.out.println(prompt.llmInput());
        JsonObject json = llm.generateJson(prompt, Deepthought.PRIMARY_LLM).get();

        //System.out.println(jsonStr);
        json.put("query", t.text());
        json.put("altes_ergebnis", t.result());
        return json;
    }

    public JsonObject evaluateThought(Thought t) {
        // JsonObject json = cache.computeIfAbsent("eval", t.id(), cid -> {
//        for (Thought sub : t.thoughts()) {
//        evaluate(t);
//        }
        return new JsonObject();
        // });
    }

}
