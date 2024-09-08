package de.jotschi.ai.deepthought;

import de.jotschi.ai.deepthought.llm.LLMConfig;
import de.jotschi.ai.deepthought.llm.ollama.OllamaService;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;

public class AbstractLLMTest {

    protected PromptService ps = new PromptService();
    protected LLMConfig config = new LLMConfig();
    protected OllamaService llm = new OllamaService(config);
    protected Deepthought dt = new Deepthought(llm, ps);

    public static String QA_QUERY = """
            Welches ist das beste Beispiel für ein Objekt, das Licht bricht?

            Mögliche Antworten:

            A) Brillen
            B) Spiegel
            C) Silberschale
            D) Fernsehger\u00e4te

            Gib nur den Buchstaben für die Antwort aus.
            """;
}
