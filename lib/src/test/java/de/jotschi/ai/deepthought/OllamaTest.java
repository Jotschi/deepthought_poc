package de.jotschi.ai.deepthought;

import org.junit.jupiter.api.Test;

import de.jotschi.ai.deepthought.llm.LLM;
import de.jotschi.ai.deepthought.llm.prompt.Prompts;
import de.jotschi.ai.deepthought.llm.prompt.impl.PromptImpl;
import io.metaloom.ai.genai.llm.prompt.Prompt;
import io.vertx.core.json.JsonObject;

public class OllamaTest extends AbstractLLMTest {

    @Test
    public void testStructuredJson() throws Exception {
        String template = """
                print some json
                """;
        Prompt prompt = new PromptImpl(template, Prompts.EVAL);
        JsonObject out = llm().generateJson(prompt, LLM.OLLAMA_MISTRAL_SMALL_32_24B_Q8).get();
        System.out.println(out.encodePrettily());
    }

}
