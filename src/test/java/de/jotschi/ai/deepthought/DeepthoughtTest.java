package de.jotschi.ai.deepthought;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.model.Thought;

/**
 * Unit test which executes the Deepthought PoC implementation.
 */
public class DeepthoughtTest extends AbstractLLMTest {

//    public static final String QUERY = "Welche Aufgabe bekommt \"Deep Thought\" und wie reagiert er auf diese Aufgabe?";

//    public static final String QUERY = "Warum sind die Delfine aus dem Buch Per Anhalter durch die Galaxis von der Erde verschwunden bevor sie zerstört wurde?";
    public static final String QUERY = "Warum sind die Delfine von der Erde verschwunden bevor sie zerstört wurde?";

    public static final String CONTEXT_1 = "Macht’s gut und danke für den Fisch (So long, and thanks for all the fish) ist der Titel des vierten Bands der fünfteiligen Romanserie Per Anhalter durch die Galaxis von Douglas Adams. Der Roman erschien 1984 im Original, die deutsche Übersetzung 1985.";

    public static final String CONTEXT_2 = "Die Delfine oder Delphine gehören zu den Zahnwalen und sind somit Säugetiere, die im Wasser leben. Delfine sind die vielfältigste und mit fast 40 Arten größte Familie der Wale. Sie sind in allen Meeren verbreitet, einige Arten kommen auch in Flüssen vor.";

    public static final String CONTEXT_3 = "Der Mensch verwüstet die Erde im wahrsten Sinne des Wortes. Die Abholzung von Wäldern, Überdüngung, zu starke Beweidung, Übernutzung durch die Landwirtschaft, falsche Bewässerungsmethoden gehören zu den wichtigsten Ursachen, für die der Mensch verantwortlich ist.";

    @Test
    public void deepThoughtTest() throws IOException {
        dt.addMockQueryContext(QUERY, List.of(CONTEXT_1, CONTEXT_2, CONTEXT_3));
        Thought t = dt.process(QUERY);
        System.out.println(t.toString());
        System.out.println("Answers:\n\n" + dt.answer(t));
    }

    @Test
    public void noDeepThoughtTest() {
        String answer = llm.generate(LLM.OLLAMA_GEMMA2_27B_INST_Q8, QUERY + " Gib aus wieviel prozent der informationen aus dem context du für die beantwortung genutzt hast. Antworte JSON", 0.3d, "json");
        System.out.println("Answer:\n\n" + answer);
    }
}
