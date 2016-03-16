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

    public Map<String, String> getData() {
        return data;
    }

    public boolean contains(String key) {
        return data.containsKey(key);
    }

    public Boolean bool(String key) {
        return null;
    }

    public Boolean bool(String key, Boolean defaultValue) {
        return null;
    }

    public Integer integer(String key) {
        return null;
    }

    public Integer integer(String key, Integer defaultValue) {
        return null;
    }

    public String str(String key) {
        return data.get(key);
    }

    public String str(String key, String defaultValue) {
        if (contains(key)) {
            return str(key);
        }
        return defaultValue;
    }

}
