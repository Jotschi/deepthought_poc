package de.jotschi.ai.deepthought.ops;

import org.junit.jupiter.api.Test;

import de.jotschi.ai.deepthought.AbstractLLMTest;
import de.jotschi.ai.deepthought.ops.impl.DeepthoughtDecomposeContextOperation;

public class DeepthoughtDecomposeContextOperationTest extends AbstractLLMTest {

    @Test
    public void testDecomposeContext() throws Exception {
        DeepthoughtDecomposeContextOperation op = new DeepthoughtDecomposeContextOperation(llm, ps);
        String out = op.process(QA_QUERY, "Welche Materialien lassen Licht ungehindert passieren?");
        System.out.println(out);
    }

}
