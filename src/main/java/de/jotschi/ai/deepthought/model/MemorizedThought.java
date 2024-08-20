package de.jotschi.ai.deepthought.model;

public class MemorizedThought {

    private DecompositionStep step;
    private String result;

    public MemorizedThought(DecompositionStep step, String result) {
        this.step = step;
        this.result = result;
    }

    public String result() {
        return result;
    }

    public DecompositionStep step() {
        return step;
    }

}
