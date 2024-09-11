package de.jotschi.ai.deepthought.llm;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import de.jotschi.ai.deepthought.llm.ollama.CachingAsyncOllamaService;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.impl.PromptImpl;
import io.vertx.core.json.JsonObject;

public class CachingAsyncOllamaServiceTest {

    private CachingAsyncOllamaService service = new CachingAsyncOllamaService(new LLMConfig());

    @Test
    public void testOneCall() throws Exception {
        Prompt prompt = new PromptImpl("Say hello and return JSON", PromptKey.EVAL);
        Future<JsonObject> out = service.generateJson(prompt, LLM.OLLAMA_LLAMA31_8B_INST_Q8);
        System.out.println(out.get().encodePrettily());
    }

    @Test
    public void testOneCallEval() throws Exception {
        Prompt prompt = new PromptImpl("Say hello and return JSON\n Format: { \"text\": \"Your output\" }", PromptKey.EVAL);
        Future<JsonObject> out = service.generateJsonAndEval(prompt, "Did the output contain a greeting?", LLM.OLLAMA_LLAMA31_8B_INST_Q8);
        System.out.println(out.get().encodePrettily());
    }

    @Test
    public void testOllama() throws InterruptedException, ExecutionException, IOException {
        Prompt prompt = new PromptImpl("Say hello", PromptKey.EVAL);
        for (int i = 0; i < 10; i++) {
            Future<String> f = service.generateText(prompt, LLM.OLLAMA_LLAMA31_8B_INST_Q8);
            Thread.startVirtualThread(() -> {
                try {
                    System.out.println("Running: " + Thread.currentThread());
                    String out = f.get();
                    System.out.println(out);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }
        System.in.read();
    }
}
