package de.jotschi.ai.deepthought.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeepthoughtMemoryEntry {

    // The query for the entry which can be decomposed
    private String query;

    // The list of decomposed steps
    private List<DecompositionStep> steps;

    // The optional context for the query
    private QueryContext context;

    // The evaluated query (using the context)
    private String result;

    public DeepthoughtMemoryEntry() {
    }

    public DeepthoughtMemoryEntry(String query, QueryContext context, List<DecompositionStep> steps, String result) {
        this.query = query;
        this.steps = steps;
        this.context = context;
        this.result = result;
    }

    @JsonProperty("result")
    public String result() {
        return result;
    }

    @JsonProperty("context")
    public QueryContext context() {
        return context;
    }

    @JsonProperty("query")
    public String query() {
        return query;
    }

    @JsonProperty("steps")
    public List<DecompositionStep> steps() {
        return steps;
    }

    public void setSteps(List<DecompositionStep> steps) {
        this.steps = steps;
    }

}
