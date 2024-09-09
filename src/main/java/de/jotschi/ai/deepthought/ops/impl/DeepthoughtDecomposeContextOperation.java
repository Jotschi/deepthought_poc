package de.jotschi.ai.deepthought.ops.impl;

import java.security.NoSuchAlgorithmException;

import de.jotschi.ai.deepthought.Deepthought;
import de.jotschi.ai.deepthought.llm.LLMContext;
import de.jotschi.ai.deepthought.llm.ollama.OllamaService;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.ops.AbstractDeepthoughtOperation;
import de.jotschi.ai.deepthought.util.HashUtil;
import de.jotschi.ai.deepthought.util.TextUtil;
import io.vertx.core.json.JsonObject;

public class DeepthoughtDecomposeContextOperation extends AbstractDeepthoughtOperation {

    public DeepthoughtDecomposeContextOperation(OllamaService llm, PromptService ps) {
        super(llm, ps);
    }

    public String process(String thought, String query) throws NoSuchAlgorithmException {
        Prompt prompt = ps.getPrompt(PromptKey.DECOMPOSE_CONTEXT);
        prompt.set("thought", thought);
        prompt.set("query", TextUtil.quote(query));
        System.out.println(prompt.llmInput());

        JsonObject json = cache.computeIfAbsent("decomp_context", HashUtil.md5(prompt.llmInput()), cid -> {
            LLMContext ctx = LLMContext.ctx(prompt, Deepthought.PRIMARY_LLM);
            String out = llm.generate(ctx, "text");
            return new JsonObject().put("text", out);
        });

        return json.getString("text");
    }

}
