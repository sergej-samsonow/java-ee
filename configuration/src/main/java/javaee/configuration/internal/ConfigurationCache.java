package javaee.configuration.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigurationCache {

    private ConcurrentHashMap<String, Map<String, String>> cache = new ConcurrentHashMap<>();

    public Map<String, String> get(String key) {
        Map<String, String> data = cache.get(key);
        return data == null ? null : new HashMap<>(cache.get(key));
    }

    public Map<String, String> store(String id, Map<String, String> data) {
        cache.putIfAbsent(id, data == null ? new HashMap<>() : new HashMap<>(data));
        return get(id);
    }
}
