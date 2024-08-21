package de.jotschi.ai.deepthought.llm.prompt;

public enum PromptKey {

    DECOMPOSE("decompose.txt"),

    DECOMPOSE_WITH_CONTEXT("decompose_with_context.txt"),

    STEP("step.txt"),

    STEP_WITH_CONTEXT("step_with_context.txt"),

    FINALIZE("finalize.txt");

    private String path;

    PromptKey(String path) {
        this.path = path;
    }

    public String promptPath() {
        return path;
    }

}
