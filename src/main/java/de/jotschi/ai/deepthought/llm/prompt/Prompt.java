package de.jotschi.ai.deepthought.llm.prompt;

import java.util.Map;

public interface Prompt {

    /**
     * Return the prompt text with applied parameters.
     * 
     * @return
     */
    String text();

    /**
     * Return the prompt key for the prompt.
     * 
     * @return
     */
    PromptKey key();

    /**
     * Set parameter into the prompt.
     * 
     * @param key
     * @param value
     */
    void set(String key, String value);

    /**
     * Get set parameters.
     * 
     * @return
     */
    Map<String, String> parameters();
}