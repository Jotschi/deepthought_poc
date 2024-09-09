package de.jotschi.ai.deepthought.llm.prompt;

public enum PromptKey {

    DECOMPOSE("decompose.txt"),

    DECOMPOSE_WITH_CONTEXT("decompose_with_context.txt"),

    EVAL("evaluate.txt"),

    ANSWER("answer.txt"),

    ANSWER_WITH_CONTEXT("answer_with_context.txt"),

    FINALIZE("finalize.txt"),

    DECOMPOSE_CONTEXT("decompose_context.txt"),

    EVAL_QA("evaluate_qa.txt");

    private String path;

    PromptKey(String path) {
        this.path = path;
    }

    public String promptPath() {
        return path;
    }

}
