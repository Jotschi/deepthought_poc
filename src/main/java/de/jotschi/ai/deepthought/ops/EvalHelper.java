package de.jotschi.ai.deepthought.ops;

import java.util.function.Function;

import de.jotschi.ai.deepthought.Deepthought;
import de.jotschi.ai.deepthought.llm.LLMContext;
import de.jotschi.ai.deepthought.llm.ollama.OllamaService;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.util.TextUtil;

public class EvalHelper {

    private static PromptService ps = new PromptService();

    public static String evaluate(OllamaService llm, String query, Function<String, String> answerProvider) {
        String best = null;
        int bestRating = 0;
        for (int i = 0; i < 5; i++) {
            String answer = null;
            try {
                answer = answerProvider.apply(query);
                //System.out.println("[" + i + "]:" + answer);
                Prompt prompt = ps.getPrompt(PromptKey.EVAL_QA);
                prompt.set("query", TextUtil.quote(query));
                prompt.set("answer", answer);
                System.out.println(prompt.llmInput());
                LLMContext ctx = LLMContext.ctx(prompt, Deepthought.PRIMARY_LLM);
                String rating = llm.generate(ctx, "text");
                int ratingNr = Integer.parseInt(rating);
                if (bestRating < ratingNr) {
                    bestRating = ratingNr;
                    best = answer;
                }
            } catch (Exception e) {
                System.out.println(answer);
                // NOOP
                e.printStackTrace();
            }
        }
        System.out.println("BEST: " + best);
        return best;
    }
}
