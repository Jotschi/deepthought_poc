package de.jotschi.ai.deepthought.dagger;

import javax.inject.Singleton;

import dagger.Provides;
import de.jotschi.ai.deepthought.llm.LLMConfig;
import io.metaloom.ai.genai.llm.ollama.OllamaLLMProvider;

@dagger.Module
public class DeepthoughtModule {

    @Provides
    @Singleton
    public OllamaLLMProvider provider() {
        return new OllamaLLMProvider();
    }

    @Provides
    @Singleton
    LLMConfig llmConfig() {
        return new LLMConfig();
    }

}
