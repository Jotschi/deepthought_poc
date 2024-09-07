package de.jotschi.ai.deepthought;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jotschi.ai.deepthought.datasource.DatasourceManager;
import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.llm.ollama.OllamaService;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;
import de.jotschi.ai.deepthought.model.Thought;
import de.jotschi.ai.deepthought.ops.impl.DeepthoughtAnswerOperation;
import de.jotschi.ai.deepthought.ops.impl.DeepthoughtDecomposeOperation;
import de.jotschi.ai.deepthought.ops.impl.DeepthoughtEvaluateOperation;

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

    public Deepthought(OllamaService llm, PromptService ps) {
        this.decomposeOp = new DeepthoughtDecomposeOperation(llm, ps);
        this.answerOp = new DeepthoughtAnswerOperation(llm, ps);
        this.evalOp = new DeepthoughtEvaluateOperation(llm, ps);
    }

    public DatasourceManager datasourceManager() {
        return dsm;
    }

    public Thought process(String query) throws IOException {
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

        // Start decompose process for each branch
        for (Thought thought : root.thoughts()) {
            decomposeOp.process(thought);
        }

        // Now evaluate the branches
        for (Thought t2 : root.thoughts()) {

            answerOp.answerThought(t2);
            evalOp.evaluateThought(t2);
//            for (Thought t3 : t2.thoughts()) {
//                evaluateThought(t3);
//            }
        }

        return root;
    }

   

    protected List<String> lookupQueryContext(String query) {
        return mockContextMap.get(query);
    }

    public void addMockQueryContext(String query, List<String> contextList) {
        this.mockContextMap.put(query, contextList);
    }

}
