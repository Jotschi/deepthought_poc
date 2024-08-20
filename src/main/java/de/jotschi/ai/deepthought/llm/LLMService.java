package de.jotschi.ai.deepthought.llm;

public interface LLMService {

    default String generate(LLMContext ctx) {
        return generate(ctx, 0.3d);
    }

    String generate(LLMContext ctx, double temperatur);

}
