package de.jotschi.ai.deepthought.ops.impl;

import de.jotschi.ai.deepthought.Deepthought;
import de.jotschi.ai.deepthought.llm.ollama.CachingAsyncOllamaService;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.ops.AbstractDeepthoughtOperation;
import de.jotschi.ai.deepthought.util.TextUtil;
import io.vertx.core.json.JsonObject;

public class DeepthoughtDecomposeContextOperation extends AbstractDeepthoughtOperation {

    public DeepthoughtDecomposeContextOperation(CachingAsyncOllamaService llm, PromptService ps) {
        super(llm, ps);
    }

    public String process(String thought, String query) throws Exception {
        Prompt prompt = ps.getPrompt(PromptKey.DECOMPOSE_CONTEXT);
        prompt.set("thought", thought);
        prompt.set("query", TextUtil.quote(query));
//        System.out.println(prompt.llmInput());

        String out = llm.generateText(prompt, Deepthought.PRIMARY_LLM).get(); 
        JsonObject json = new JsonObject().put("text", out);

        return json.getString("text");
    }

}
