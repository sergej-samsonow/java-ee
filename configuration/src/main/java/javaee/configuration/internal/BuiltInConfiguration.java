package javaee.configuration.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import javaee.configuration.event.builtinconfiguration.ErrorOnPropertiesLoad;
import javaee.configuration.event.builtinconfiguration.PropertiesFileNotFound;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class BuiltInConfiguration {

    private ConcurrentHashMap<String, Map<String, String>> cache = new ConcurrentHashMap<>();

    @Inject
    private Event<ErrorOnPropertiesLoad> ioException;

    @Inject
    private Event<PropertiesFileNotFound> propertiesNotFound;

    protected String id(Class<?> clazz, String collection) {
        return clazz.getName() + "." + collection;
    }

    protected Map<String, String> cache(String id) {
        return new HashMap<>(cache.get(id));
    }

    protected String path(String collection) {
        return "/" + collection + ".properties";
    }

    protected Properties newProperties() {
        return new Properties();
    }

    protected InputStream stream(Class<?> clazz, String collection) {
        return clazz.getResourceAsStream(collection);
    }

    protected Properties properties(InputStream stream, String path) {
        Properties properties = newProperties();
        try {
            properties.load(stream);
        }
        catch (IOException e) {
            ioException.fire(new ErrorOnPropertiesLoad(path, e));
            properties = newProperties();
        }
        return properties;
    }

    protected Map<String, String> map(Properties properties) {
        Map<String, String> data = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            data.put(key, properties.getProperty(key));
        }
        return data;
    }

    protected Map<String, String> store(String id, Map<String, String> data) {
        cache.putIfAbsent(id, new HashMap<>(data));
        return cache(id);
    }

    public @NotNull Map<String, String> data(Class<?> clazz, String collection) {
        Map<String, String> data = new HashMap<>();
        if (clazz == null || collection == null) {
            return data;
        }
        String id = id(clazz, collection);
        data = cache(id);
        if (data != null) {
            return data;
        }

        data = new HashMap<>();
        String path = path(collection);
        InputStream stream = stream(clazz, path);
        if (stream == null) {
            propertiesNotFound.fire(new PropertiesFileNotFound(path));
            return store(id, data);
        }
        return store(id, map(properties(stream, path)));
    }
}
