package EngineManager;

import CoreParts.api.Engine;
import CoreParts.impl.InnerSystemComponents.EngineImpl;

import java.util.HashMap;
import java.util.Map;

public class EngineManager {

    private Map<String, EngineImpl> engines = new HashMap<>();

    EngineManager() {
        // Initialize the engines
        engines.put("Engine1", new EngineImpl());
    }

    public Engine getEngine(String engineName) {
        return engines.get(engineName);
    }
}
