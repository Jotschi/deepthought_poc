package de.jotschi.ai.deepthought.ops;

import io.vertx.core.json.JsonObject;

public class EvalResult<T> {

    public final String x;
    public final T y;

    public EvalResult(String x, T y) {
        this.x = x;
        this.y = y;
    }

    public static EvalResult<JsonObject> fromJson(String key, JsonObject json) {
        return new EvalResult<>(json.getString(key), json);
    }
}
