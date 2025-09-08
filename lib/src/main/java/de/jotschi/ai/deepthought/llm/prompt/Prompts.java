package de.jotschi.ai.deepthought.llm.prompt;

import io.metaloom.ai.genai.llm.prompt.PromptKey;

public enum Prompts implements PromptKey {

    DECOMPOSE("decompose"),

    DECOMPOSE_WITH_CONTEXT("decompose_with_context"),

    EVAL("evaluate"),

    ANSWER("answer"),

    ANSWER_WITH_CONTEXT("answer_with_context"),

    FINALIZE("finalize"),

    DECOMPOSE_CONTEXT("decompose_context"),

    EVAL_QA("evaluate_qa");

    private String id;

    Prompts(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }
}
