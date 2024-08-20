package de.jotschi.ai.deepthought.model;

import java.util.List;

public class Decomposition {

    private String query;

    private List<DecompositionStep> steps;

    public String getQuery() {
        return query;
    }

    public List<DecompositionStep> getSteps() {
        return steps;
    }

}
