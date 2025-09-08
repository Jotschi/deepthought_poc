package de.jotschi.ai.deepthought.ops;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.jotschi.ai.deepthought.JSON;
import de.jotschi.ai.deepthought.llm.ollama.CachingAsyncOllamaService;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;

public abstract class AbstractDeepthoughtOperation implements DeepthoughtOperation {

    protected CachingAsyncOllamaService llm;
    protected PromptService ps;

    public static ObjectMapper mapper = JSON.getMapper();

    public AbstractDeepthoughtOperation(CachingAsyncOllamaService llm, PromptService ps) {
        this.llm = llm;
        this.ps = ps;
    }
}
