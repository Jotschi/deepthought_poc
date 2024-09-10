package de.jotschi.ai.deepthought.ops;

import de.jotschi.ai.deepthought.Deepthought;
import de.jotschi.ai.deepthought.cache.LLMCache;
import de.jotschi.ai.deepthought.llm.LLMContext;
import de.jotschi.ai.deepthought.llm.ollama.OllamaService;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.util.HashUtil;
import de.jotschi.ai.deepthought.util.TextUtil;
import io.vertx.core.json.JsonObject;

public class EvalHelper {

    private static PromptService ps = new PromptService();
    private static LLMCache cache = new LLMCache();

    public static String evaluate(OllamaService llm, String query, EvaluationMethod answerProvider) {
        return evaluate(llm, query, null, answerProvider);
    }

    public static String evaluate(OllamaService llm, String query, String context, EvaluationMethod answerProvider) {
        String best = null;
        int bestRating = 0;
        for (int i = 0; i < 5; i++) {
            String answer = null;
            try {
                answer = answerProvider.create(query);
                // System.out.println("[" + i + "]:" + answer);
                Prompt prompt = ps.getPrompt(PromptKey.EVAL_QA);
                prompt.set("query", TextUtil.quote(query));
                prompt.set("answer", answer);
                prompt.set("context", context == null ? "" : "Kontext:\n" + context);
                // System.out.println(prompt.llmInput());
                LLMContext ctx = LLMContext.ctx(prompt, Deepthought.PRIMARY_LLM);
                final String finAnswer = answer;
                String rating = llm.generate(ctx, "text");
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
