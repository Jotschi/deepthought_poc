package de.jotschi.ai.deepthought.ops.impl;

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
import de.jotschi.ai.deepthought.util.TextUtil;
import io.vertx.core.json.JsonObject;

public class DeepthoughtEvaluateOperation extends AbstractDeepthoughtOperation {

    public DeepthoughtEvaluateOperation(OllamaService llm, PromptService ps) {
        super(llm, ps);
    }

    public JsonObject evaluate(Thought t) {
        Prompt prompt = null;
        prompt = ps.getPrompt(PromptKey.EVAL);

        String expert = t.expert();
        if (expert != null) {
            prompt.set("expert", expert);
        }
        prompt.set("query", t.text());
        prompt.set("result", t.result());
        LLMContext ctx = LLMContext.ctx(prompt, Deepthought.PRIMARY_LLM);

        StringBuilder builder = new StringBuilder();
        for (Thought sub : t.parent().thoughts()) {
            if (sub.id().equals(t.id())) {
                continue;
            }
            builder.append("# " + sub.text() + ":\n" + TextUtil.quote(sub.result()) + "\n\n");
        }
        prompt.set("extra", builder.toString());
        System.out.println(prompt.llmInput());
        String jsonStr = llm.generate(ctx, "json");

        System.out.println(jsonStr);
        JsonObject json = new JsonObject(jsonStr);
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

    @Override
    public void process(Thought thought) throws JsonMappingException, JsonProcessingException {
        // TODO Auto-generated method stub
        
    }

}
