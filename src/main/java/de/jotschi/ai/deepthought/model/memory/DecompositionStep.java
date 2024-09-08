package de.jotschi.ai.deepthought.model.memory;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DecompositionStep {

    @JsonProperty("query_flag")
    private boolean queryFlag;

    @JsonProperty("query_text")
    private String queryText;

    @JsonProperty("query_type")
    private String queryType;

    private int relevance;

    private String text;

    private String context;

    private boolean processable;

    private String expert;

    public String getExpert() {
        return expert;
    }

    public boolean isQueryFlag() {
        return queryFlag;
    }

    public String getQueryText() {
        return queryText;
    }

    public String getQueryType() {
        return queryType;
    }

    public int getRelevance() {
        return relevance;
    }

    public String getText() {
        return text;
    }

    public boolean isProcessable() {
        return processable;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

}
