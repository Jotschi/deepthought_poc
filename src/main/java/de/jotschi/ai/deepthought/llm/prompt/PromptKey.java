package de.jotschi.ai.deepthought.llm.prompt;

public enum PromptKey {

    DECOMPOSE("decompose.txt"),

    DECOMPOSE_WITH_CONTEXT("decompose_with_context.txt"),

    EVAL("evaluate.txt"),

    ANSWER("answer.txt"),

    ANSWER_WITH_CONTEXT("answer_with_context.txt"),

    FINALIZE("finalize.txt");

    private String path;

    PromptKey(String path) {
        this.path = path;
    }

    public String promptPath() {
        return path;
    }

}
