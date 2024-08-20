package de.jotschi.ai.deepthought.llm.prompt;

public enum PromptKey {

    DECOMPOSE("decompose.txt"),

    STEP("step.txt"), 
    
    FINALIZE("finalize.txt");

    
    private String path;

    PromptKey(String path) {
        this.path = path;
    }

    public String promptPath() {
        return path;
    }

}
