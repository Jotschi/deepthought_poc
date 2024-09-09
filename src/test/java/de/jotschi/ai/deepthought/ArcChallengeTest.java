package de.jotschi.ai.deepthought;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import de.jotschi.ai.deepthought.cache.JsonCache;
import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.util.HashUtil;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ArcChallengeTest extends AbstractLLMTest {

    private JsonCache cache = new JsonCache();

    @Test
    public void testQA_Mercury_SC_407695() throws IOException, NoSuchAlgorithmException {
        String question = "Juan und LaKeisha rollen einige Objekte eine Rampe hinunter. Sie möchten sehen, welches Objekt am weitesten rollt. Was sollten sie tun, damit sie ihre Untersuchung wiederholen können?";

        String choices = """
                A) Die Objekte in Gruppen einteilen.
                B) Die Höhe der Rampe ändern.
                C) Verschiedene Objekte zum Rollen auswählen.
                D) Die Details der Untersuchung aufzeichnen.
                """;

        // Answer Key is D
        String out = answer(question, choices);
        System.out.println(out);
    }

    @Test
    public void testEvalAllWithOllama() throws IOException {
        AtomicLong correct = new AtomicLong(0);
        AtomicLong checked = new AtomicLong(0);
        processDataset(entry -> {
            try {
                String id = entry.getString("id");
                String answerKey = entry.getString("answerKey");
                String question = entry.getString("question");
                String choices = entry.getString("choices");
                LLM languageModel = LLM.OLLAMA_LLAMA31_8B_INST_Q8;
                String evalId = HashUtil.md5(id + "_" + languageModel.key());
                JsonObject result = cache.computeIfAbsent("eval-arc_direct", evalId, cid -> {
                    String query = toQuery(question, choices);
                    // System.out.println(query);
                    String out = llm.generate(languageModel, query, 0.3f, "text");
                    return new JsonObject().put("id", id).put("answer", out).put("answerKey", answerKey);
                });

                if (answerKey.equalsIgnoreCase(result.getString("answer").trim())) {
                    correct.incrementAndGet();
                }
                // System.out.println("Result: " + result.encodePrettily());
                // System.in.read();
                checked.incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        float factor = correct.floatValue() / checked.floatValue();
        System.out.printf("Result: %.2f\n", factor);
    }

    @Test
    public void testEvalAllWithDeepthought() throws IOException {
        AtomicLong correct = new AtomicLong(0);
        AtomicLong checked = new AtomicLong(0);
        processDataset(entry -> {
            try {
                String id = entry.getString("id");
                String answerKey = entry.getString("answerKey");
                String question = entry.getString("question");
                String choices = entry.getString("choices");

                String out = answer(question, choices);
                // System.out.println(out);

                if (answerMatches(answerKey, out)) {
                    correct.incrementAndGet();
                }
                // System.out.println("Result: " + result.encodePrettily());
//                System.in.read();
                checked.incrementAndGet();

                float factor = correct.floatValue() / checked.floatValue();
                System.out.printf("Result: %.2f\n", factor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // float factor = correct.floatValue() / checked.floatValue();
        // System.out.printf("Result: %.2f", factor);
    }

    private boolean answerMatches(String expectedAnswer, String out) {
        if (out.trim().length() == 1 && out.equalsIgnoreCase(expectedAnswer)) {
            return true;
        }
        if (out.trim().endsWith(" " + expectedAnswer)) {
            return true;
        }
        if (out.trim().endsWith(" " + expectedAnswer + ".")) {
            return true;
        }
        if (out.trim().equalsIgnoreCase(expectedAnswer + ")")) {
            return true;
        }
        if (out.trim().contains(" " + expectedAnswer + ")")) {
            return true;
        }
        if (out.trim().contains(" (" + expectedAnswer + ")")) {
            return true;
        }

        // System.out.println("FALSCH: [" + expectedAnswer + "] " + out);
//        try {
//            System.in.read();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        return false;
    }

    private void processDataset(Consumer<JsonObject> entryCallback) throws IOException {
        String datasetFile = "ArcChallenge_de/test-00000-of-00001-a0c917350be4ccd9.parquet.jsonl";
        List<String> lines = Files.readAllLines(Path.of(datasetFile));
        for (String line : lines) {
            try {
                JsonObject entry = new JsonObject(line);
                // System.out.println(entry.encodePrettily());
                String question = entry.getString("question_de");
                StringBuilder choices = new StringBuilder();
                JsonArray choiceTexts = entry.getJsonObject("choices_de").getJsonArray("text");
                JsonArray choiceLabels = entry.getJsonObject("choices_de").getJsonArray("label");
                for (int i = 0; i < choiceTexts.size(); i++) {
                    String label = choiceLabels.getString(i);
                    String text = choiceTexts.getString(i);
                    choices.append(label + ") " + text + "\n");
                }

                JsonObject entryJson = new JsonObject();
                entryJson.put("id", entry.getString("id"));
                entryJson.put("answerKey", entry.getString("answerKey"));
                entryJson.put("question", question);
                entryJson.put("choices", choices);
                entryCallback.accept(entryJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String answer(String question, String choices) throws IOException, NoSuchAlgorithmException {
        String query = toQuery(question, choices);
        return dt.process(query).result();
    }

    private String toQuery(String question, String choices) {
        return question + "\nMögliche Antworten sind:\n" + choices + "\nGib nur den Buchstaben der Antwort ohne weiter Erklärung aus.";
    }
}
