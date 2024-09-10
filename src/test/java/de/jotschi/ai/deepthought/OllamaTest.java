package de.jotschi.ai.deepthought;

import org.junit.jupiter.api.Test;

import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.llm.prompt.Prompt;
import de.jotschi.ai.deepthought.llm.prompt.PromptKey;
import de.jotschi.ai.deepthought.llm.prompt.impl.PromptImpl;
import io.vertx.core.json.JsonObject;

public class OllamaTest extends AbstractLLMTest {

    @Test
    public void testStructuredJson() throws Exception {
        String template = """
                print some json
                """;
        Prompt prompt = new PromptImpl(template, PromptKey.EVAL);
        JsonObject out = llm.generateJson(prompt, LLM.OLLAMA_LLAMA31_8B_INST_Q8).get();
        System.out.println(out.encodePrettily());
    }

}
