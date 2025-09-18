package de.jotschi.ai.deepthought.dagger;

import javax.inject.Singleton;

import at.apa.vertx.apex.dagger.module.JsonModule;
import at.apa.vertx.apex.dagger.module.VertxModule;
import dagger.BindsInstance;
import dagger.Component;
import de.jotschi.ai.deepthought.chat.DeepthoughtChat;
import de.jotschi.ai.deepthought.config.DeepthoughtConfig;
import de.jotschi.ai.deepthought.llm.ollama.CachingAsyncOllamaService;

@Singleton
@Component(modules = { VertxModule.class, JsonModule.class, DeepthoughtModule.class })
public interface DeepthoughtComponent {

    // CommandLine cli();

    @Component.Builder
    interface Builder {

        /**
         * Inject configuration options.
         * 
         * @param options
         * @return
         */
        @BindsInstance
        Builder configuration(DeepthoughtConfig config);

        /**
         * Build the component.
         * 
         * @return
         */
        DeepthoughtComponent build();

    }

    CachingAsyncOllamaService ollama();

    DeepthoughtChat chat();

}
