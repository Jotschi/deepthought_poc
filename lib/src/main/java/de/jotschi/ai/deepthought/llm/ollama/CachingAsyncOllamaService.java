package de.jotschi.ai.deepthought.llm.ollama;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.jotschi.ai.deepthought.Deepthought;
import de.jotschi.ai.deepthought.cache.LLMCache;
import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.llm.prompt.Prompts;
import de.jotschi.ai.deepthought.ops.EvalResult;
import de.jotschi.ai.deepthought.ops.EvaluationMethod;
import de.jotschi.ai.deepthought.util.TextUtil;
import io.metaloom.ai.genai.llm.LLMContext;
import io.metaloom.ai.genai.llm.prompt.Prompt;
import io.metaloom.ai.genai.utils.TextUtils;
import io.vertx.core.json.JsonObject;

@Singleton
public class CachingAsyncOllamaService {

    private OllamaService ollama;

    private static final Semaphore POOL = new Semaphore(2);

    private static PromptService ps = new PromptService();

    private LLMCache cache = new LLMCache();

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Inject
    public CachingAsyncOllamaService(OllamaService ollama) {
        this.ollama = ollama;
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
            try {
                POOL.acquire();
                LLMContext ctx = LLMContext.ctx(prompt, llm);
                String out = ollama.generate(ctx, format);
                out = TextUtils.extractJson(out);
                System.out.println(out);
                cache.submit(prompt, llm, out);
                return mapping.apply(out);
            } catch (InterruptedException e) {
                throw new RuntimeException("Failed to handle semaphore", e);
            } finally {
                POOL.release();
            }
        });

    }

    public Future<JsonObject> generateJsonAndEval(Prompt prompt, String query, LLM llm) {
        JsonObject result = evaluate(query, null, q -> {
            JsonObject json = generateJson(prompt, llm).get();
            return EvalResult.fromJson("text", json);
        });
        return CompletableFuture.completedFuture(result);
    }

    public <T> T evaluate(String query, String context, EvaluationMethod<T> answerProvider) {
        T best = null;
        int bestRating = 0;
        for (int i = 0; i < 5; i++) {
            T answer = null;
            try {
                EvalResult<T> result = answerProvider.create(query);
                answer = result.y;
                // System.out.println("[" + i + "]:" + answer);
                Prompt prompt = ps.getPrompt(Prompts.EVAL_QA);
                prompt.set("query", TextUtil.quote(query));
                prompt.set("answer", result.x);
                prompt.set("context", context == null ? "" : "Kontext:\n" + context);
                // System.out.println(prompt.llmInput());
                final T finAnswer = answer;
                String rating = generateText(prompt, Deepthought.PRIMARY_LLM).get();
                int ratingNr = Integer.parseInt(rating);
                JsonObject json = new JsonObject().put("rating", ratingNr).put("answer", finAnswer);
                if (bestRating < ratingNr) {
                    bestRating = ratingNr;
                    best = answer;
                }
            } catch (Exception e) {
                // System.out.println(answer);
                // NOOP
                e.printStackTrace();
            }
        }
        // System.out.println("BEST: " + best);
        return best;
    }

}
