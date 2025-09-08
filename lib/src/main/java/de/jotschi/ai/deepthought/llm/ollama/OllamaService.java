package de.jotschi.ai.deepthought.llm.ollama;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.llm.LLMConfig;
import de.jotschi.ai.deepthought.llm.LLMService;
import dev.langchain4j.model.ollama.OllamaModel;
import dev.langchain4j.model.ollama.OllamaModels;
import io.metaloom.ai.genai.llm.LLMContext;
import io.metaloom.ai.genai.llm.LargeLanguageModel;
import io.metaloom.ai.genai.llm.ollama.OllamaLLMProvider;
import io.metaloom.ai.genai.llm.prompt.Prompt;

@Singleton
public class OllamaService implements LLMService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);

    private LLMConfig config;

    private OllamaLLMProvider ollama;

    @Inject
    public OllamaService(LLMConfig config, OllamaLLMProvider ollama) {
        this.config = config;
        this.ollama = ollama;
    }

    public String generate(LLM llm, String promptStr, double temperature, String format) {
        String url = llm.primaryHost() != null ? llm.primaryHost() : config.getOllamaAPIUrl();
        // logger.info("Using {} for {}", url, llm);
        Prompt prompt = null;
        LLM model = null;
        LLMContext ctx = LLMContext.ctx(prompt, llm);
        return ollama.generate(ctx);
    }

    public String generateText(LLMContext ctx, double temperature) {
        return generate(ctx, temperature, "text");
    }

    @Override
    public String generate(LLMContext ctx, double temperature, String format) {
        LargeLanguageModel llm = ctx.model();
        String url = llm.url();
        // logger.info("Using {} for {}", url, llm);
//        ChatLanguageModel model = OllamaChatModel.builder()
//                .baseUrl(url)
//                .timeout(Duration.ofMinutes(15))
//                .modelName(ctx.llmModel().key())
//                // .numPredict(4096)
//                .format(format)
//                .temperature(temperature)
//                .build();

        return ollama.generate(ctx);
    }

    public void listModels(LLMContext ctx) {
        LargeLanguageModel llm = ctx.model();
        String url = llm.url();
        // logger.info("Using {} for {}", url, llm);
        OllamaModels models = OllamaModels.builder().baseUrl(url).build();
        for (OllamaModel model : models.availableModels().content()) {
            System.out.println(model.getName());
        }
    }

}
