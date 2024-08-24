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

}
