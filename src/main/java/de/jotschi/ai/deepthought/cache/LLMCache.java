package de.jotschi.ai.deepthought.cache;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.util.HashUtil;

public class LLMCache {

    private static final Logger logger = LoggerFactory.getLogger(LLMCache.class);
    public static boolean enabled = true;
    private static Path CACHE_FOLDER = Path.of("cache");

//    public String computeIfAbsent(String prefix, String id, Function<String, String> mappingFunction) {
//        if (!enabled) {
//            return mappingFunction.apply(id);
//        }
//    }

    private Path toCachePath(String prefix, String id) throws NoSuchAlgorithmException {
        return Paths.get(prefix, id.substring(0, 4), id + ".json");
    }

//    public String computeIfAbsent(Prompt prompt, LLM llm, Function<String, JsonObject> mappingFunction) throws NoSuchAlgorithmException {
//        String id = HashUtil.md5(prompt.llmInput() + "_" + llm.name());
//        return computeIfAbsent(prompt.key().name(), id, mappingFunction);
//    }

    public String get(Prompt prompt, LLM llm) {
        // TODO Auto-generated method stub
        return null;
    }

    public String submit(Prompt prompt, LLM llm, String value) throws NoSuchAlgorithmException {
        if (value == null) {
            return null;
        }
        if (!enabled) {
            return value;
        }
        String prefix = prompt.key().name();
        String id = cache(prompt, llm);
        try {
            Path cachePath = CACHE_FOLDER.resolve(toCachePath(prefix, id));
            if (Files.exists(cachePath)) {
                return FileUtils.readFileToString(cachePath.toFile(), Charset.defaultCharset());
            } else {
                File cacheParentFolder = cachePath.toFile().getParentFile();
                cacheParentFolder.mkdirs();
                FileUtils.writeStringToFile(cachePath.toFile(), value, Charset.defaultCharset());
                return value;
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error("Failed to compute caching key", e);
            throw new RuntimeException(e);
        }
    }

    private String cache(Prompt prompt, LLM llm) throws NoSuchAlgorithmException {
        return HashUtil.md5(prompt.llmInput() + "_" + llm.name());
    }

}
