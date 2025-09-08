package de.jotschi.ai.deepthought.llm;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import de.jotschi.ai.deepthought.AbstractLLMTest;
import de.jotschi.ai.deepthought.llm.prompt.Prompts;
import de.jotschi.ai.deepthought.llm.prompt.impl.PromptImpl;
import io.metaloom.ai.genai.llm.prompt.Prompt;
import io.vertx.core.json.JsonObject;

public class CachingAsyncOllamaServiceTest extends AbstractLLMTest {

    @Test
    public void testOneCall() throws Exception {
        Prompt prompt = new PromptImpl("Say hello and return JSON", Prompts.EVAL);
        Future<JsonObject> out = llm().generateJson(prompt, LLM.OLLAMA_MISTRAL_SMALL_32_24B_Q8);
        System.out.println(out.get().encodePrettily());
    }

    @Test
    public void testOneCallEval() throws Exception {
        Prompt prompt = new PromptImpl("Say hello and return JSON\n Format: { \"text\": \"Your output\" }", Prompts.EVAL);
        Future<JsonObject> out = llm().generateJsonAndEval(prompt, "Did the output contain a greeting?", LLM.OLLAMA_MISTRAL_SMALL_32_24B_Q8);
        System.out.println(out.get().encodePrettily());
    }

    @Test
    public void testOllama() throws InterruptedException, ExecutionException, IOException {
        Prompt prompt = new PromptImpl("Say hello", Prompts.EVAL);
        for (int i = 0; i < 10; i++) {
            Future<String> f = llm().generateText(prompt, LLM.OLLAMA_MISTRAL_SMALL_32_24B_Q8);
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
