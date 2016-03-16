package javaee.configuration;

import java.util.Map;

public class Collection {

    private String name;
    private Map<String, String> data;

    public Collection(String name, Map<String, String> data) {
        super();
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    // FIXME immutable map
    public Map<String, String> getData() {
        return data;
    }

    public boolean contains(String key) {
        return data.containsKey(key);
    }

    public Boolean bool(String key) {
        String content = data.get(key);
        return content != null ? Boolean.parseBoolean(content) : null;
    }

    // TODO return not null
    public Boolean bool(String key, Boolean defaultValue) {
        if (contains(key)) {
            return bool(key);
        }
        return defaultValue;
    }

    // TODO return value or boolean true
    // boolOrTrue(String key);
    // boolOrFalse(String key);

    // TODO return null and trigger cdi event on parse exception
    public Integer integer(String key) {
        String content = data.get(key);
        return content != null ? Integer.parseInt(content) : null;
    }

    // TODO return not null
    public Integer integer(String key, Integer defaultValue) {
        if (contains(key)) {
            return integer(key);
        }
        return defaultValue;
    }

    public String str(String key) {
        return data.get(key);
    }

    // TODO return not null
    public String str(String key, String defaultValue) {
        if (contains(key)) {
            return str(key);
        }
        return defaultValue;
    }

}
