package de.jotschi.ai.deepthought.ops.impl;

import java.security.NoSuchAlgorithmException;

import de.jotschi.ai.deepthought.Deepthought;
import de.jotschi.ai.deepthought.llm.LLMContext;
import de.jotschi.ai.deepthought.llm.ollama.OllamaService;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.model.Thought;
import de.jotschi.ai.deepthought.ops.AbstractDeepthoughtOperation;
import de.jotschi.ai.deepthought.util.HashUtil;
import de.jotschi.ai.deepthought.util.TextUtil;
import io.vertx.core.json.JsonObject;

public class DeepthoughtFinalizeOperation extends AbstractDeepthoughtOperation {

    public DeepthoughtFinalizeOperation(OllamaService llm, PromptService ps) {
        super(llm, ps);
    }

    public JsonObject finalizeAnswer(Thought branch) throws NoSuchAlgorithmException {
        StringBuilder contextBuilder = new StringBuilder();

        Prompt prompt = ps.getPrompt(PromptKey.FINALIZE);
        LLMContext ctx = LLMContext.ctx(prompt, Deepthought.PRIMARY_LLM);
        double score = 0f;
        
        Thought subThought = branch.thoughts().getLast();
        contextBuilder.append("\n\n# " + subThought.text() + ":\n\n" + TextUtil.quote(subThought.result()));
        /*
        for (Thought subThought : branch.thoughts()) {
            contextBuilder.append("\n\n# " + subThought.text() + ":\n\n" + TextUtil.quote(subThought.result()));
            score += subThought.score();
        }
        */
        prompt.set("summary_query", branch.getSummaryQuery());
        prompt.set("feedback", contextBuilder.toString());
        prompt.setText(branch.text());
        
        System.out.println("Finalize Prompt: " + prompt.llmInput());
        
        String cacheKeyValue = HashUtil.md5(PromptKey.FINALIZE.name() + "_" + Deepthought.PRIMARY_LLM.key() + "_" + branch.text() + "_" + contextBuilder.toString());

        final double outScore = score;
        JsonObject json = cache.computeIfAbsent("final", cacheKeyValue, cid -> {
            String txt = llm.generate(ctx, "text");
            JsonObject jsonOut = new JsonObject();
            jsonOut.put("score", outScore);
            jsonOut.put("text", txt);
            return jsonOut;
        });

        return json;
    }

}
