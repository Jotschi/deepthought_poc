package de.jotschi.ai.deepthought.llm.ollama;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

import de.jotschi.ai.deepthought.cache.LLMCache;
import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.llm.LLMConfig;
import de.jotschi.ai.deepthought.llm.LLMContext;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import io.vertx.core.json.JsonObject;

public class CachingAsyncOllamaService {

    private OllamaService ollama;

    private static final Semaphore POOL = new Semaphore(2);

    private LLMCache cache = new LLMCache();

//    private static Thread.Builder builder = Thread.ofVirtual().name("LLM-Pool");
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public CachingAsyncOllamaService(LLMConfig config) {
        this.ollama = new OllamaService(config);
    }

    public Future<String> generateText(Prompt prompt, LLM llm) {
        return generate(prompt, "text", llm, Function.identity());
    }

    public Future<JsonObject> generateJson(Prompt prompt, LLM llm) {
        return generate(prompt, "json", llm, JsonObject::new);
    }

    private <T> Future<T> generate(Prompt prompt, String format, LLM llm, Function<String, T> mapping) {
        String cached = cache.get(prompt, llm);
        if (cached != null) {
            return CompletableFuture.completedFuture(mapping.apply(cached));
        }
        return executor.submit(() -> {
            System.out.println("Start");
            try {
                POOL.acquire();
                System.out.println("Ready");
                LLMContext ctx = LLMContext.ctx(prompt, llm);
                String out = ollama.generate(ctx, format);
                cache.submit(prompt, llm, out);
                return mapping.apply(out);
            } catch (InterruptedException e) {
                throw new RuntimeException("Failed to handle semaphore", e);
            } finally {
                POOL.release();
            }
        });

    }

}
