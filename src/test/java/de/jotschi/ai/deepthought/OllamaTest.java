package de.jotschi.ai.deepthought;

import org.junit.jupiter.api.Test;

import de.jotschi.ai.deepthought.llm.LLM;

public class OllamaTest extends AbstractLLMTest {

    @Test
    public void testStructuredJson() {

        String prompt = """
                print some json
                """;
        String out = llm.generate(LLM.OLLAMA_LLAMA31_8B_INST_Q8, prompt, 0.3f, "json");
        System.out.println(out);
    }

}
