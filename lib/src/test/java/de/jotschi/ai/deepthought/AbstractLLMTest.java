package de.jotschi.ai.deepthought;

import org.junit.jupiter.api.BeforeEach;

import de.jotschi.ai.deepthought.config.DeepthoughtConfig;
import de.jotschi.ai.deepthought.dagger.DaggerDeepthoughtComponent;
import de.jotschi.ai.deepthought.dagger.DeepthoughtComponent;
import de.jotschi.ai.deepthought.llm.ollama.CachingAsyncOllamaService;
import de.jotschi.ai.deepthought.llm.prompt.PromptService;

public class AbstractLLMTest {

    protected PromptService ps = new PromptService();
    protected Deepthought dt = null;

    private DeepthoughtComponent dtc;
    private DeepthoughtConfig config;

    @BeforeEach
    public void setup() {
        config = new DeepthoughtConfig();
        dtc = DaggerDeepthoughtComponent.builder().configuration(config).build();
    }

    public static String QA_QUERY = """
            Welches ist das beste Beispiel für ein Objekt, das Licht bricht?

            Mögliche Antworten:

            A) Brillen
            B) Spiegel
            C) Silberschale
            D) Fernsehger\u00e4te

            Gib nur den Buchstaben für die Antwort aus.
            """;

    public static String QA_QUERY_2 = """
            Welches Verfahren bestimmt am besten, ob die Wassertemperatur die Zeit beeinflusst, die ein Zuckerwürfel zum Auflösen benötigt?

            Mögliche Antworten:

            A) Testen Sie drei Zuckerwürfel, jeweils einen in drei verschiedenen Wassertemperaturen.
            B) Testen Sie drei Zuckerwürfel in einer Wassertemperatur.
            C) Testen Sie einen zerkleinerten Zuckerwürfel und einen ganzen Zuckerwürfel im Wasser.
            D) Testen Sie drei Zuckerwürfel, jeweils einen in einer Säure, einer Base und Wasser.

            Gib nur den Buchstaben für die Antwort aus.
            """;

    public CachingAsyncOllamaService llm() {
        return dtc.ollama();
    }

}
