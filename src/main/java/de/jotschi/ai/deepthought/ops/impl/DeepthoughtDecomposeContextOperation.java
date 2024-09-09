package de.jotschi.ai.deepthought.ops.impl;

import de.jotschi.ai.deepthought.Deepthought;
import de.jotschi.ai.deepthought.llm.LLMContext;
import de.jotschi.ai.deepthought.llm.ollama.OllamaService;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.ops.AbstractDeepthoughtOperation;
import de.jotschi.ai.deepthought.util.TextUtil;

public class DeepthoughtDecomposeContextOperation extends AbstractDeepthoughtOperation {

    public DeepthoughtDecomposeContextOperation(OllamaService llm, PromptService ps) {
        super(llm, ps);
    }

    public String process(String thought, String query) {
        Prompt prompt = ps.getPrompt(PromptKey.DECOMPOSE_CONTEXT);
        prompt.set("thought", thought);
        prompt.set("query",  TextUtil.quote(query));
        System.out.println(prompt.llmInput());
        LLMContext ctx = LLMContext.ctx(prompt, Deepthought.PRIMARY_LLM);
        return llm.generate(ctx, "text");
    }

}
