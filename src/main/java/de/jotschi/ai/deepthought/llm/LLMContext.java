package de.jotschi.ai.deepthought.llm;

import de.jotschi.ai.deepthought.llm.prompt.Prompt;

public class LLMContext {

    /**
     * Parameter that will limit how much tokens the llm will be generating in the
     * completion phase. This must not exceed the model output maximum. (Usually
     * 4096 tokens)
     */
    private int tokenOutputLimit = 4096;

    private final LLM model;

    private final Prompt prompt;

    public LLMContext(LLM model, Prompt prompt) {
        this.model = model;
        this.prompt = prompt;
    }

    public Prompt prompt() {
        return prompt;
    }

    public LLM llmModel() {
        return model;
    }

    public int tokenOutputLimit() {
        return tokenOutputLimit;
    }

    public void setTokenOutputLimit(int tokenOutputLimit) {
        this.tokenOutputLimit = tokenOutputLimit;
    }

    public static LLMContext ctx(Prompt prompt, LLM model) {
        return new LLMContext(model, prompt);
    }

}
