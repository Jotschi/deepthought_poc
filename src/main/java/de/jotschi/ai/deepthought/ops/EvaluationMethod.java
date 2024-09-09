package de.jotschi.ai.deepthought.ops;

@FunctionalInterface
public interface EvaluationMethod {

    String create(String question) throws Exception;

}
