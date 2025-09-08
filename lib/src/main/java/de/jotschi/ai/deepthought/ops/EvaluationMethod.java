package de.jotschi.ai.deepthought.ops;

@FunctionalInterface
public interface EvaluationMethod<T> {

    EvalResult<T> create(String question) throws Exception;

}
