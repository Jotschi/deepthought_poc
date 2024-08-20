package de.jotschi.ai.deepthought.llm.prompt.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;

public class PromptImpl implements Prompt {

    private final String text;
    private final PromptKey key;
    private final Map<String, String> parameters = new HashMap<>();

    public PromptImpl(String text, PromptKey key) {
        this.text = text;
        this.key = key;
    }

    @Override
    public String text() {
        String output = text;
        // Poor Mans Template Handling
        for (Entry<String, String> entry : parameters.entrySet()) {
            output = output.replaceAll("\\$\\{" + entry.getKey() + "\\}", entry.getValue());
        }
        return output;
    }

    @Override
    public PromptKey key() {
        return key;
    }

    @Override
    public Map<String, String> parameters() {
        return parameters;
    }

    @Override
    public void set(String key, String value) {
        parameters.put(key, value);
    }

    @Override
    public String toString() {
        return key() + "\n" + text();
    }

}
