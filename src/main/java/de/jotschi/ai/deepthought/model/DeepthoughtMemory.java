package de.jotschi.ai.deepthought.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.jotschi.ai.deepthought.Deepthought;

public class DeepthoughtMemory {

    private Deepthought dt;

    private List<MemorizedThought> thoughts = new ArrayList<>();
    private String query;

    public DeepthoughtMemory(Deepthought dt) {
        this.dt = dt;
    }

    public void process(Decomposition decomp) throws IOException {
        int id = 1;
        for (DecompositionStep step : decomp.getSteps()) {
            String result = dt.evalStep(id++, query, step);
            thoughts.add(new MemorizedThought(step, result));
        }
    }

    public List<MemorizedThought> thoughts() {
        return thoughts;
    }

    public String query() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}
