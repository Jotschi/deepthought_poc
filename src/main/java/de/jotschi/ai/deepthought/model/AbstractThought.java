package de.jotschi.ai.deepthought.model;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.jotschi.ai.deepthought.util.HashUtil;

public abstract class AbstractThought implements Thought {

    private final String text;

    private String context;

    private String expert;

    private List<Thought> thoughts = new ArrayList<>();

    private String result;

    private int confidence;

    private Thought parent;

    private String summaryQuery;

    public AbstractThought(String text, String context) {
        this.text = text;
        this.context = context;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public String context() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public List<Thought> thoughts() {
        return thoughts;
    }

    @Override
    public void add(Thought thought) {
        if (thoughts == null) {
            thoughts = new ArrayList<>();
        }
        thoughts.add(thought);
        thought.setParent(this);
    }

    @Override
    public Thought setParent(Thought thought) {
        this.parent = thought;
        return this;
    }

    @Override
    public Thought parent() {
        return parent;
    }

    @Override
    public String result() {
        return result;
    }

    @Override
    public Thought setResult(String result) {
        this.result = result;
        return this;
    }

    @Override
    public int confidence() {
        return confidence;
    }

    @Override
    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }

    @Override
    public String expert() {
        return expert;
    }

    @Override
    public Thought setExpert(String expert) {
        this.expert = expert;
        return this;
    }

    @Override
    public String getSummaryQuery() {
        return summaryQuery;
    }

    @Override
    public void setSummaryQuery(String summaryQuery) {
        this.summaryQuery = summaryQuery;
    }

    @Override
    public int score() {
        int score = 0;
        if (context() != null) {
            score = confidence;
        }
        System.out.println("[" + id() + "] " + score + "  " + context());
        for (Thought t : thoughts()) {
            score += t.score();
        }
        return score;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int level) {
        StringBuilder builder = new StringBuilder();
        builder.append("[" + text() + "]\n");
        if (context() != null) {
            String contextStr = StringUtils.leftPad("    CTX:" + context(), (level * 4) + context().length());
            builder.append(contextStr + "\n");
        }
        level++;
        for (Thought t : thoughts()) {
            String unpadded = t.toString(level);
            String str = StringUtils.leftPad(" * " + unpadded, (level * 4) + unpadded.length());
            builder.append(str + "\n");

        }
        return builder.toString();
    }

    @Override
    public String id() {
        try {
            return HashUtil.md5(text + context);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
