package javaee.configuration.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import javaee.configuration.event.BuiltInConfigurationErrorOnLoad;
import javaee.configuration.event.BuiltInConfigurationNotFound;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class BuiltInConfiguration {

    private PropertiesReader reader = new PropertiesReader();
    private ConfigurationCache cache = new ConfigurationCache();

    @Inject
    private Event<BuiltInConfigurationErrorOnLoad> errorOnLoad;

    @Inject
    private Event<BuiltInConfigurationNotFound> notFound;

    protected String id(Class<?> clazz, String collection) {
        return clazz.getName() + "." + collection;
    }

    protected String path(String collection) {
        return "/" + collection + ".properties";
    }

    protected InputStream stream(Class<?> clazz, String collection) {
        return clazz.getResourceAsStream(collection);
    }

    public @NotNull Map<String, String> data(Class<?> clazz, String collection) {
        // validate parameter
        Map<String, String> data;
        if (clazz == null || collection == null) {
            return new HashMap<>();
        }

        // return cached data if exists
        String id = id(clazz, collection);
        data = cache.get(id);
        if (data != null) {
            return data;
        }

        // open stream
        data = new HashMap<>();
        String path = path(collection);
        InputStream stream = stream(clazz, path);
        if (stream == null) {
            notFound.fire(new BuiltInConfigurationNotFound(path));
            return cache.store(id, data);
        }

        // read stream
        try {
            data = reader.read(stream);
        }
        catch (IOException e) {
            errorOnLoad.fire(new BuiltInConfigurationErrorOnLoad(path, e));
        }
        return cache.store(id, data);
    }
}
