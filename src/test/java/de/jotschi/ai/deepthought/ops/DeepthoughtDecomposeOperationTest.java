package de.jotschi.ai.deepthought.ops;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.jotschi.ai.deepthought.AbstractLLMTest;
import de.jotschi.ai.deepthought.model.Thought;
import de.jotschi.ai.deepthought.ops.impl.DeepthoughtDecomposeOperation;

public class DeepthoughtDecomposeOperationTest extends AbstractLLMTest {

    @Test
    public void testDecompose() throws JsonMappingException, JsonProcessingException {
        DeepthoughtDecomposeOperation op = new DeepthoughtDecomposeOperation(llm, ps);
        // Thought t = Thought.of("Warum sind die Delfine von der Erde verschwunden
        // bevor sie zerstört wurde?");
        Thought t = Thought.of(QA_QUERY);
        op.process(t);
        for (Thought subt : t.thoughts()) {
            System.out.println("[" + subt.id() + "] " + subt.text());
        }
        //assertEquals(7, t.thoughts().size());
    }
}
