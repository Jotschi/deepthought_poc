package de.jotschi.ai.deepthought.cache;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class JsonCache {

    private static final Logger logger = LoggerFactory.getLogger(JsonCache.class);

    public JsonCache() {

    }

    public JsonObject computeIfAbsent(String prefix, String id, Function<String, JsonObject> mappingFunction) {
        Path cacheFolder = Path.of("cache");
        try {
            Path cachePath = cacheFolder.resolve(toCachePath(prefix, id));
            if (Files.exists(cachePath)) {
                String str = FileUtils.readFileToString(cachePath.toFile(), Charset.defaultCharset());
                return new JsonObject(str);
            } else {
                File cacheParentFolder = cachePath.toFile().getParentFile();
                JsonObject mappedValue = mappingFunction.apply(id);
                if (mappedValue != null) {
                    cacheParentFolder.mkdirs();
                    FileUtils.writeStringToFile(cachePath.toFile(), mappedValue.encodePrettily(), Charset.defaultCharset());
                    return mappedValue;
                }
                throw new RuntimeException("Failed to generate a valid output.");
            }
        } catch (NoSuchAlgorithmException | IOException e) {
            logger.error("Failed to compute caching key", e);
            throw new RuntimeException(e);
        }
    }

    private Path toCachePath(String prefix, String id) throws NoSuchAlgorithmException {
        String hash = md5(id);
        return Paths.get(prefix, hash.substring(0, 4), hash);
    }

    private String md5(String text) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(StandardCharsets.UTF_8.encode(text));
        return String.format("%032x", new BigInteger(1, md5.digest()));
    }

}
