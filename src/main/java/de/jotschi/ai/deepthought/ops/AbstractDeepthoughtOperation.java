package de.jotschi.ai.deepthought.ops;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.jotschi.ai.deepthought.JSON;
import de.jotschi.ai.deepthought.cache.JsonCache;
import de.jotschi.ai.deepthought.llm.ollama.OllamaService;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;

public abstract class AbstractDeepthoughtOperation implements DeepthoughtOperation {

    protected OllamaService llm;
    protected PromptService ps;

    public static ObjectMapper mapper = JSON.getMapper();
    protected JsonCache cache = new JsonCache();

    public AbstractDeepthoughtOperation(OllamaService llm, PromptService ps) {
        this.llm = llm;
        this.ps = ps;
    }
}
