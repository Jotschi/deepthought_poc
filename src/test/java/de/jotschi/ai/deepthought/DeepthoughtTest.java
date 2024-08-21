package de.jotschi.ai.deepthought;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.model.QueryContext;

/**
 * Unit test which executes the Deepthought PoC implementation.
 */
public class DeepthoughtTest extends AbstractLLMTest {

//    public static final String QUERY = "Welche Aufgabe bekommt \"Deep Thought\" und wie reagiert er auf diese Aufgabe?";

//    public static final String QUERY = "Warum sind die Delfine aus dem Buch Per Anhalter durch die Galaxis von der Erde verschwunden bevor sie zerstört wurde?";
    public static final String QUERY = "Warum sind die Delfine von der Erde verschwunden bevor sie zerstört wurde?";

    public static final String CONTEXT_1 = "Macht’s gut und danke für den Fisch (So long, and thanks for all the fish) ist der Titel des vierten Bands der fünfteiligen Romanserie Per Anhalter durch die Galaxis von Douglas Adams. Der Roman erschien 1984 im Original, die deutsche Übersetzung 1985.";;

    @Test
    public void deepThoughtTest() throws IOException {
        dt.setMockQueryContextList(List.of(QueryContext.create(CONTEXT_1)));
        String answer = dt.process(QUERY);
        System.out.println("Answer: " + answer);
    }

    @Test
    public void noDeepThoughtTest() {
        String answer = llm.generate(LLM.OLLAMA_GEMMA2_27B_INST_Q8, QUERY, 0.3d);
        System.out.println("Answer:\n\n" + answer);
    }
}
