package de.jotschi.ai.deepthought;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.junit.jupiter.api.Disabled;
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
    @Disabled
    public void deepThoughtTest() throws IOException, NoSuchAlgorithmException {
        dt.addMockQueryContext(QUERY, List.of(CONTEXT_1, CONTEXT_2, CONTEXT_3));
        Thought t = dt.process(QUERY);
        System.out.println(t.toString());
    }

    @Test
    public void testQA() throws IOException, NoSuchAlgorithmException {
        String answer = dt.process(QA_QUERY).result();
        System.out.println(answer);
    }

    @Test
    @Disabled
    public void testEvaluate() {
        Thought t = Thought.of("Warum sind die Delfine von der Erde verschwunden bevor sie zerstört wurde?");
        Thought sub1 = Thought.of("Wo spielt der Roman 'Macht's gut und danke für den Fisch'").setResult(
                "Der Roman 'Macht's gut und danke für den Fisch' von Douglas Adams spielt hauptsächlich auf der Erde, genauer gesagt in England.  Es gibt aber auch Szenen, die auf anderen Planeten im Universum stattfinden, wie z.B. Magrathea.");
        Thought sub2 = Thought.of("Finde Informationen über das Verschwinden der Delfine im Roman").setResult(
                "Um dir Informationen zum Verschwinden der Delfine im Roman zu liefern, brauche ich den Titel des Romans. Bitte nenne mir den Titel, damit ich dir weiterhelfen kann.");

        t.add(sub1);
        t.add(sub2);
        dt.evaluateThought(sub2);
//        t.add(Thought.of("Wird die Erde in 'Macht's gut und danke für den Fisch' zerstört?").setResult(
//                "Ja, am Ende von Douglas Adams' 'Per Anhalter durch die Galaxis'-Reihe, in dem Buch 'Macht's gut und danke für den Fisch', wird die Erde von den Vogonen zerstört, um Platz für eine Umgehungsstraße zu machen."));
    }

    @Test
    @Disabled
    public void noDeepThoughtTest() {
        String answer = llm.generate(LLM.OLLAMA_GEMMA2_27B_INST_Q8, QUERY + " Gib aus wieviel prozent der informationen aus dem context du für die beantwortung genutzt hast. Antworte JSON",
                0.3d, "json");
        System.out.println("Answer:\n\n" + answer);
    }
}
