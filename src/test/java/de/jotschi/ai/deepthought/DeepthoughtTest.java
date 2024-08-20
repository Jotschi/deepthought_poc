package de.jotschi.ai.deepthought;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.jotschi.ai.deepthought.model.Decomposition;

public class DeepthoughtTest extends AbstractLLMTest {

//    public static final String QUERY = "Welche Aufgabe bekommt \"Deep Thought\" und wie reagiert er auf diese Aufgabe?";

    // public static final String QUERY="Warum ist die Antwort von \"Deep Thought\"
    // für die Konstrukteure unbefriedigend und was fordern sie stattdessen?";

    public static final String QUERY = "Warum sind die Delphine aus dem Buch Per Anhalter durch die Galaxis von der Erde verschwunden bevor sie zerstört wurde?";

    @Test
    public void deepThoughtTest() throws IOException {
        dt.datasourceManager().put("Welches Buch ist 'Per Anhalter durch die Galaxis'?",
                "\"Per Anhalter durch die Galaxis\" (im englischen Original: \"The Hitchhiker's Guide to the Galaxy\") ist ein Buch des britischen Autors Douglas Adams. Es ist der erste Teil einer Science-Fiction-Reihe, die oft als \"Trilogie in fünf Bänden\" bezeichnet wird, obwohl sie tatsächlich aus fünf Büchern besteht.");

        dt.datasourceManager().put("Wann haben die Delphine in 'Per Anhalter durch die Galaxis' verschwunden?\n",
                "In \"Per Anhalter durch die Galaxis\" (Originaltitel: The Hitchhiker's Guide to the Galaxy) von Douglas Adams verschwinden die Delfine kurz bevor die Erde zerstört wird. Sie verlassen den Planeten, weil sie von der bevorstehenden Zerstörung durch die Vogonen wissen, die die Erde sprengen, um Platz für eine Hyperraum-Umgehungsstraße zu schaffen.");

        dt.datasourceManager().put("Warum haben die Delphine in 'Per Anhalter durch die Galaxis' verschwunden?",
                "In Douglas Adams' „Per Anhalter durch die Galaxis“ verschwinden die Delfine von der Erde, weil sie wissen, dass der Planet kurz davor steht, zerstört zu werden. Die Delfine sind in der Geschichte intelligenter als die Menschen und sind sich der drohenden Gefahr bewusst.");

        dt.datasourceManager().put("Sind die Delphine in 'Per Anhalter durch die Galaxis' zerstört worden?",
                "Nein, die Delphine in \"Per Anhalter durch die Galaxis\" sind nicht zerstört worden. Im Gegenteil, sie haben die Erde rechtzeitig verlassen, bevor diese durch die Vogonen zerstört wurde, um Platz für eine hyperraum-schnellstraße zu machen.");

        dt.datasourceManager().put("Sind die Delphine in 'Per Anhalter durch die Galaxis' vor ihrem Verschwinden zerstört worden?", "Nein");
        
        dt.memory().setQuery(QUERY);
        Decomposition decomp = dt.decompose(QUERY);
        dt.memory().process(decomp);
        System.out.println("\n\nAnswer:\n" + dt.answer());
    }

//    @Test
//    public void noDeepThoughtTest() {
//        String answer = llm.generate(LLM.OLLAMA_LLAMA31_8B_INST_Q8, QUERY, 0.3d);
//        System.out.println("Answer:\n\n" + answer);
//    }
//    
//    @Test
//    public void noDeepThoughtTest2() {
//        String answer = llm.generate(LLM.OLLAMA_LLAMA31_70B_INST_Q4_K_S, QUERY, 0.3d);
//        System.out.println("Answer:\n\n" + answer);
//    }

}

//Was ist das Ziel von den Konstrukteuren des Computers "Deep Thought", als sie ihn bauen?
//Welche Aufgabe bekommt "Deep Thought" und wie reagiert er auf diese Aufgabe?
//Wie löst "Deep Thought" die gestellte Aufgabe und welche Antwort gibt er?
//Warum ist die Antwort von "Deep Thought" für die Konstrukteure unbefriedigend und was fordern sie stattdessen?
//Welche Überraschung erlebt "Deep Thought" am Ende, als er die Frage beantwortet?