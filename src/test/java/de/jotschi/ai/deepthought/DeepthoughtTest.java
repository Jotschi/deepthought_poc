package de.jotschi.ai.deepthought;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.model.Decomposition;

/**
 * Unit test which executes the Deepthought PoC implementation.
 */
public class DeepthoughtTest extends AbstractLLMTest {

//    public static final String QUERY = "Welche Aufgabe bekommt \"Deep Thought\" und wie reagiert er auf diese Aufgabe?";

    public static final String QUERY = "Warum sind die Delfine aus dem Buch Per Anhalter durch die Galaxis von der Erde verschwunden bevor sie zerstört wurde?";

    @Test
    public void deepThoughtTest() throws IOException {
        dt.memory().setQuery(QUERY);
        Decomposition decomp = dt.decompose(QUERY);
        dt.memory().process(decomp);
        System.out.println("\n\nAnswer:\n" + dt.answer());
    }

    @Test
    public void noDeepThoughtTest() {
        String answer = llm.generate(LLM.OLLAMA_LLAMA31_8B_INST_Q8, QUERY, 0.3d);
        System.out.println("Answer:\n\n" + answer);
    }
}
