package de.jotschi.ai.deepthought.model.memory;

import java.util.ArrayList;
import java.util.List;

public class DecompositionResult {

    private String query;
    private List<DecompositionStep> steps = new ArrayList<>();

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<DecompositionStep> getSteps() {
        return steps;
    }

    public void setSteps(List<DecompositionStep> steps) {
        this.steps = steps;
    }

}
