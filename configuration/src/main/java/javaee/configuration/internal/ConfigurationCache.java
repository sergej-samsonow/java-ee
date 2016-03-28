package javaee.configuration.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationCache {

    private ConcurrentHashMap<String, Map<String, String>> cache = new ConcurrentHashMap<>();

    public Map<String, String> get(String key) {
        return new HashMap<>(cache.get(key));
    }

    public Map<String, String> store(String id, Map<String, String> data) {
        cache.putIfAbsent(id, new HashMap<>(data));
        return get(id);
    }
}
