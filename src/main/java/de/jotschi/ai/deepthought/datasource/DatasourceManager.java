package de.jotschi.ai.deepthought.datasource;

import java.util.HashMap;
import java.util.Map;

import de.jotschi.ai.deepthought.model.DecompositionStep;
import io.vertx.core.json.JsonObject;

public class DatasourceManager {

    private Map<String, String> mockDatasource = new HashMap<>();

    public DatasourceManager() {
    }

    public JsonObject process(DecompositionStep step) {
        String text = step.getQueryText();
        String type = step.getQueryType();
        if (mockDatasource.containsKey(text)) {
            return new JsonObject().put("text", mockDatasource.get(text));
        } else {
            System.out.println(text);
            throw new RuntimeException("Mock datasource does not contain '" + text + "' entry. Type: " + type);
        }
    }

    public void put(String key, String value) {
        mockDatasource.put(key, value);
    }

}
