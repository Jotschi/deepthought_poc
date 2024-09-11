package de.jotschi.ai.deepthought;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jotschi.ai.deepthought.datasource.DatasourceManager;
import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.llm.ollama.CachingAsyncOllamaService;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.model.Thought;
import de.jotschi.ai.deepthought.ops.impl.DeepthoughtAnswerOperation;
import de.jotschi.ai.deepthought.ops.impl.DeepthoughtDecomposeOperation;
import de.jotschi.ai.deepthought.ops.impl.DeepthoughtEvaluateOperation;
import de.jotschi.ai.deepthought.ops.impl.DeepthoughtFinalizeOperation;
import io.vertx.core.json.JsonObject;

public class Deepthought {

    // private static final LLM PRIMARY_LLM = LLM.OLLAMA_GEMMA2_27B_INST_Q8;
    public static final LLM PRIMARY_LLM = LLM.OLLAMA_LLAMA31_8B_INST_Q8;
    public static final int MAX_DEPTH = 3;
    public static boolean RECURSIVE = false;

    private DatasourceManager dsm = new DatasourceManager();

    private Map<String, List<String>> mockContextMap = new HashMap<>();

    // Operations
    private DeepthoughtDecomposeOperation decomposeOp;
    private DeepthoughtAnswerOperation answerOp;
    private DeepthoughtEvaluateOperation evalOp;
    private DeepthoughtFinalizeOperation finalOp;

    public Deepthought(CachingAsyncOllamaService llm, PromptService ps) {
        this.decomposeOp = new DeepthoughtDecomposeOperation(llm, ps);
        this.answerOp = new DeepthoughtAnswerOperation(llm, ps);
        this.evalOp = new DeepthoughtEvaluateOperation(llm, ps);
        this.finalOp = new DeepthoughtFinalizeOperation(llm, ps);
    }

    public DatasourceManager datasourceManager() {
        return dsm;
    }

    /**
     * Decompose the query and process each step sequentially.
     * 
     * @param query
     * @return
     * @throws Exception
     */
    public Thought process(String query) throws Exception {
        Thought root = Thought.of(query);
        // Load an initial set chunks that might be relevant to the query
        List<String> contextList = lookupQueryContext(query);

        // Add the initial root entries
        if (contextList != null && !contextList.isEmpty()) {
            for (String context : contextList) {
                root.add(Thought.of(query, context));
            }
        } else {
            root.add(Thought.of(query));
        }

        // 1. Decompose for each branch
        for (Thought branchThought : root.thoughts()) {
            decomposeOp.process(branchThought);
            System.out.println("Decompose for branch complete");

            // 2. Now answer all steps of this branch
            Thought prev = null;
            for (Thought step : branchThought.thoughts()) {
                answerOp.answerThought(step, prev);
                step = prev;
            }

            // 3. Finalize the answer for the branch
            JsonObject out = finalOp.finalizeAnswer(branchThought);
            String answer = out.getString("text");
            branchThought.setResult(answer);
            if (root.thoughts().size() == 1) {
                root.setResult(answer);
            }
        }

        return root;
    }

    protected List<String> lookupQueryContext(String query) {
        return mockContextMap.get(query);
    }

    public void addMockQueryContext(String query, List<String> contextList) {
        this.mockContextMap.put(query, contextList);
    }

    public void evaluateThought(Thought t) {
        evalOp.evaluateThought(t);
    }

}
