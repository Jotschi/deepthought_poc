package de.jotschi.ai.deepthought.llm.prompt.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;

public class PromptImpl implements Prompt {

    private final String template;
    private final PromptKey key;
    private final Map<String, String> parameters = new HashMap<>();
    private String text;

    public PromptImpl(String template, PromptKey key) {
        this.template = template;
        this.key = key;
    }

    @Override
    public String template() {
        return template;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String llmInput() {
        String output = template;
        // Poor Mans Template Handling
        for (Entry<String, String> entry : parameters.entrySet()) {
            output = output.replaceAll("\\$\\{" + entry.getKey() + "\\}", entry.getValue());
        }
        if (text() != null) {
            output += text();
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
        return key() + "\n" + template();
    }

}
