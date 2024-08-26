package de.jotschi.ai.deepthought.llm.ollama;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.llm.LLMConfig;
import de.jotschi.ai.deepthought.llm.LLMContext;
import de.jotschi.ai.deepthought.llm.LLMService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaModel;
import dev.langchain4j.model.ollama.OllamaModels;

public class OllamaService implements LLMService {

    private static final Logger logger = LoggerFactory.getLogger(OllamaService.class);

    private LLMConfig config;

    public OllamaService(LLMConfig config) {
        this.config = config;
    }

    public String generate(LLM llm, String prompt, double temperature, String format) {
        System.err.println("---------");
        String url = llm.primaryHost() != null ? llm.primaryHost() : config.getOllamaAPIUrl();
        logger.info("Using {} for {}", url, llm);
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl(url)
                .timeout(Duration.ofMinutes(15))
                .modelName(llm.key())
                .format(format)
                .numPredict(4096)
                .temperature(temperature)
                .build();

        return model.generate(prompt);
    }

    @Override
    public String generate(LLMContext ctx, double temperature, String format) {
        System.err.println("---------");
        LLM llm = ctx.llmModel();
        String url = llm.primaryHost() != null ? llm.primaryHost() : config.getOllamaAPIUrl();
        logger.info("Using {} for {}", url, llm);
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl(url)
                .timeout(Duration.ofMinutes(15))
                .modelName(ctx.llmModel().key())
                .numPredict(4096)
                .format(format)
                .temperature(temperature)
                .build();
        
        String input = ctx.prompt().llmInput();

//        System.out.println("---------");
//        System.out.println(input);
//        System.out.println("---------");
        return model.generate(input);
    }

    public void listModels(LLMContext ctx) {
        LLM llm = ctx.llmModel();
        String url = llm.primaryHost() != null ? llm.primaryHost() : config.getOllamaAPIUrl();
        logger.info("Using {} for {}", url, llm);
        OllamaModels models = OllamaModels.builder().baseUrl(url).build();
        for (OllamaModel model : models.availableModels().content()) {
            System.out.println(model.getName());
        }
    }

}
