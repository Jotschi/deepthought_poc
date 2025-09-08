package de.jotschi.ai.deepthought.model.memory;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DecompositionResult {

    private String query;
    private List<DecompositionStep> steps = new ArrayList<>();

    @JsonProperty("summary_query")
    private String summaryQuery;

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

    public String getSummaryQuery() {
        return summaryQuery;
    }

}
