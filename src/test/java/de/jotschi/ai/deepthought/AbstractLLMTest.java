package de.jotschi.ai.deepthought;

import de.jotschi.ai.deepthought.llm.LLMConfig;
import de.jotschi.ai.deepthought.llm.ollama.OllamaService;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;

public class AbstractLLMTest {

    protected PromptService ps = new PromptService();
    protected LLMConfig config = new LLMConfig();
    protected OllamaService llm = new OllamaService(config);
    protected Deepthought dt = new Deepthought(llm, ps); 
}
