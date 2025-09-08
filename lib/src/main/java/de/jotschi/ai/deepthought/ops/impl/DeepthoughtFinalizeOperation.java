package de.jotschi.ai.deepthought.ops.impl;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import de.jotschi.ai.deepthought.Deepthought;
import de.jotschi.ai.deepthought.llm.ollama.CachingAsyncOllamaService;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.llm.prompt.Prompts;
import de.jotschi.ai.deepthought.model.Thought;
import de.jotschi.ai.deepthought.ops.AbstractDeepthoughtOperation;
import de.jotschi.ai.deepthought.util.HashUtil;
import de.jotschi.ai.deepthought.util.TextUtil;
import io.metaloom.ai.genai.llm.LLMContext;
import io.metaloom.ai.genai.llm.prompt.Prompt;
import io.vertx.core.json.JsonObject;

public class DeepthoughtFinalizeOperation extends AbstractDeepthoughtOperation {

    public DeepthoughtFinalizeOperation(CachingAsyncOllamaService llm, PromptService ps) {
        super(llm, ps);
    }

    public JsonObject finalizeAnswer(Thought branch) throws NoSuchAlgorithmException, InterruptedException, ExecutionException {
        StringBuilder contextBuilder = new StringBuilder();

        Prompt prompt = ps.getPrompt(Prompts.FINALIZE);
        LLMContext ctx = LLMContext.ctx(prompt, Deepthought.PRIMARY_LLM);
        double score = 0f;

        // Thought subThought = branch.thoughts().getLast();
        // contextBuilder.append("\n\n# " + subThought.text() + ":\n\n" +
        // TextUtil.quote(subThought.result()));

        for (Thought subThought : branch.thoughts()) {
            contextBuilder.append("\n\n# " + subThought.text() + "\n\n" + TextUtil.quote(subThought.result()));
            score += subThought.score();
        }

        prompt.set("summary_query", branch.getSummaryQuery());
        prompt.set("feedback", contextBuilder.toString());
        prompt.setText(branch.text());

        // System.out.println("Finalize Prompt: " + prompt.llmInput());

        String cacheKeyValue = HashUtil.md5(Prompts.FINALIZE.name() + "_" + Deepthought.PRIMARY_LLM.key() + "_" + branch.text() + "_" + contextBuilder.toString());

        final double outScore = score;
        String txt = llm.generateText(prompt, Deepthought.PRIMARY_LLM).get();
        JsonObject jsonOut = new JsonObject();
        jsonOut.put("score", outScore);
        jsonOut.put("text", txt);
        jsonOut.put("prompt", prompt.input());
        return jsonOut;

    }

}
