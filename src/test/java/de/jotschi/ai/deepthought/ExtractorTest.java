package de.jotschi.ai.deepthought;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ExtractorTest extends AbstractLLMTest {

    String name = "out_8092541_2";

//    @Test
//    public void testLLM() {
//        Prompt prompt = ps.getPrompt(PromptKey.DECOMPOSE);
//        LLMContext ctx = LLMContext.ctx(prompt, LLM.OLLAMA_LLAMA31_70B_INST_Q4_K_S);
//        ctx.setText("Nenne das Geburtsjahr und den Geburtsort von Dieter Steinwender.");
////        llm.listModels(ctx);
//        String out = llm.generate(ctx);
//        System.out.println(out);
//    }

    @Test
    public void testExtractor() {
        int r = 0;
        try (Scanner scanner = new Scanner(new File("wikipedia-22-12-de-dpr", "train.jsonl"))) {
            while (scanner.hasNextLine()) {
                String jsonLine = scanner.nextLine();
                r++;
//                if (r < 630) {
//                    continue;
//                }
                JsonObject json = new JsonObject(jsonLine);
                String wikiId = json.getString("wiki_id");
                String title = json.getString("title");
                if (!title.toLowerCase().contains("weltraum")) {
                    continue;
                }
                System.out.println(json.encodePrettily());
                JsonArray queries = json.getJsonArray("imperative_informal");
                if (queries == null) {
                    continue;
                }
                for (int i = 0; i < queries.size(); i++) {
                    String query = json.getString("title") + "\n" + queries.getString(i);
                    System.out.println(query);
                    for (int e = 0; e < 10; e++) {
                        try {
                            String contextUuid = json.getString("context_uuid");
                            String id = wikiId + "_" + i;
//                            dt.decompose(id, query);
                            break;
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    // System.in.read();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
