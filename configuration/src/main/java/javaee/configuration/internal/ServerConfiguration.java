package javaee.configuration.internal;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.enterprise.event.Event;
import javax.validation.constraints.NotNull;

import javaee.configuration.event.ServerConfigurationErrorOnLoad;
import javaee.configuration.event.ServerConfigurationIsDisabled;
import javaee.configuration.event.ServerConfigurationNotFound;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ServerConfiguration {

    private PropertiesReader reader = new PropertiesReader();
    private ConfigurationCache cache = new ConfigurationCache();

    private Event<ServerConfigurationNotFound> notFound;
    private Event<ServerConfigurationErrorOnLoad> errorOnLoad;
    private Event<ServerConfigurationIsDisabled> isDisabled;

    @Resource(mappedName = "java:comp/env/configuration")
    private String folder;

    protected InputStream stream(Path path) throws IOException {
        return new FileInputStream(path.toFile());
    }

    public Path path(String collection) {
        return Paths.get(folder, collection + ".properties");
    }

    @PostConstruct
    protected void prepare() {
        if (folder == null) {
            isDisabled.fire(new ServerConfigurationIsDisabled());
        }
    }

    public @NotNull Map<String, String> data(String collection) {

        // validate parameter
        Map<String, String> data = new HashMap<>();
        if (folder == null || collection == null) {
            return data;
        }

        // return cached data if exists
        data = cache.get(collection);
        if (data != null) {
            return data;
        }
        data = new HashMap<>();

        // check files exists
        Path path = path(collection);
        if (!Files.exists(path)) {
            notFound.fire(new ServerConfigurationNotFound(collection, path.toString()));
            return cache.store(collection, data);
        }

        // open and read file stream
        try {
            data = reader.read(stream(path));
        }
        catch (IOException e) {
            errorOnLoad.fire(new ServerConfigurationErrorOnLoad(collection, path.toString(), e));
        }
        return cache.store(collection, data);
    }
}
