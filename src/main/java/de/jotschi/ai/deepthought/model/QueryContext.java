package de.jotschi.ai.deepthought.model;

public class QueryContext {

    private String text;

    public QueryContext(String text) {
        this.text = text;
    }

    public static QueryContext create(String text) {
        return new QueryContext(text);
    }

    public String getText() {
        return text;
    }

}
