package de.jotschi.ai.deepthought;

import java.io.IOException;

import de.jotschi.ai.deepthought.config.DeepthoughtConfig;
import de.jotschi.ai.deepthought.dagger.DaggerDeepthoughtComponent;
import de.jotschi.ai.deepthought.dagger.DeepthoughtComponent;

public class DeepthoughtCLI {

    public static void main(String[] args) throws IOException {
        DeepthoughtConfig config = new DeepthoughtConfig();
        DeepthoughtComponent c = DaggerDeepthoughtComponent.builder().configuration(config).build();
        
        c.chat().run();
    }
}
