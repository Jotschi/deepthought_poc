package de.jotschi.ai.deepthought.llm;

public interface LLMService {

    default String generate(LLMContext ctx, String format) {
        return generate(ctx, 0.3d, format);
    }

    String generate(LLMContext ctx, double temperatur, String format);

}
