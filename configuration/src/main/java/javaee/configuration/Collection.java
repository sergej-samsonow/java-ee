package javaee.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import javaee.configuration.event.collection.InvalidIntegerValue;

public class Collection {

    public static final String DEFAULT_STRING_VALUE = "";
    public static final Integer DEFAULT_INTEGER_VALUE = 0;
    public static final Boolean DEFAULT_BOOLEAN_VALUE = false;

    private String name;
    private Map<String, String> data;

    @Inject
    private Event<InvalidIntegerValue> invalidIntegerValue;

    public Collection(String name, Map<String, String> data) {
        super();
        this.name = name;
        this.data = new HashMap<>(data);
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getData() {
        return new HashMap<>(data);
    }

    public boolean contains(String key) {
        return data.containsKey(key);
    }

    public Boolean bool(String key) {
        String content = data.get(key);
        return content != null ? Boolean.parseBoolean(content) : null;
    }

    public String str(String key) {
        return data.get(key);
    }

    public Integer integer(String key) {
        String content = data.get(key);
        if (content != null) {
            try {
                return Integer.parseInt(content);
            }
            catch (NumberFormatException e) {
                invalidIntegerValue.fire(new InvalidIntegerValue(getName(), key, content, e));
            }
        }
        return null;
    }

    public @NotNull Boolean boolOrTrue(String key) {
        Boolean entry = bool(key);
        return entry != null ? entry : true;
    }

    public @NotNull Boolean boolOrFalse(String key) {
        Boolean entry = bool(key);
        return entry != null ? entry : false;
    }

    public @NotNull Boolean bool(String key, Boolean defaultValue) {
        Boolean entry = bool(key);
        if (entry == null && defaultValue == null) {
            return Collection.DEFAULT_BOOLEAN_VALUE;
        }
        if (entry == null) {
            return defaultValue;
        }
        return entry;
    }

    public @NotNull Integer integer(String key, Integer defaultValue) {
        Integer entry = integer(key);
        if (entry == null && defaultValue == null) {
            return DEFAULT_INTEGER_VALUE;
        }
        else if (entry == null) {
            return defaultValue;
        }
        return entry;
    }

    public @NotNull String str(String key, String defaultValue) {
        String entry = str(key);
        if (entry == null && defaultValue == null) {
            return DEFAULT_STRING_VALUE;
        }
        else if (entry == null) {
            return defaultValue;
        }
        return entry;
    }
}
