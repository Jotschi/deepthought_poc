package de.jotschi.ai.deepthought.llm.prompt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jotschi.ai.deepthought.llm.prompt.impl.PromptImpl;

public class PromptService {

    private final Logger logger = LoggerFactory.getLogger(PromptService.class);

    private static Map<String, Prompt> PROMPT_CACHE = new ConcurrentHashMap<>();

    public PromptService() {
    }

    public Prompt getPrompt(PromptKey promptKey) {
        return PROMPT_CACHE.computeIfAbsent(promptKey.name(), key -> {
            try {
                logger.info("Loading user prompt for key {}", promptKey);
                String prompt = loadPrompt(promptKey.promptPath());
                return new PromptImpl(prompt, promptKey);
            } catch (Exception e) {
                logger.error("Failed to load prompt", e);
                throw new RuntimeException("Failed to load prompt for key " + promptKey);
            }
        });

    }

    private String loadPrompt(String path) throws IOException {
        String fullPath = "/prompts/" + path;
        try (InputStream ins = PromptService.class.getResourceAsStream(fullPath)) {
            Objects.requireNonNull(ins, "Prompt could not be found " + fullPath);
            return IOUtils.toString(ins, Charset.defaultCharset());
        }
    }

}
