package de.jotschi.ai.deepthought.model;

import java.util.List;

public interface Thought {

    String text();

    String context();

    String expert();

    Thought setExpert(String expert);

    String result();

    Thought setResult(String result);

    List<Thought> thoughts();

    int confidence();

    void setConfidence(int confidence);

    /**
     * Add a new follow-up thought
     * 
     * @param thought
     */
    void add(Thought thought);

    String id();

    String toString(int level);

    /**
     * Return the accumulated scrore for this though branch
     * 
     * @return
     */
    int score();

    static Thought of(String query) {
        return new ThoughtImpl(query);
    }

    static Thought of(String query, String context) {
        return new ThoughtImpl(query, context);
    }

}
