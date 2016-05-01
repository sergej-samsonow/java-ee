package javaee.configuration.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.event.Event;

import javaee.configuration.event.ServerConfigurationErrorOnLoad;
import javaee.configuration.event.ServerConfigurationNotFound;

public class ServerConfiguration extends PropertiesLoaderProcess {

    private String folder;
    private Event<ServerConfigurationNotFound> notFoundEvent;
    private Event<ServerConfigurationErrorOnLoad> erroOnLoadEvent;
    private Path path;

    public void setFolder(String folder) {
        this.folder = folder;
    }

    @Override
    public void prepare() {
        createPath();
        if (pathExists()) {
            enable();
            return;
        }
        eventOnNotFound();
        disable();
    }

    public void setNotFoundEvent(Event<ServerConfigurationNotFound> notFoundEvent) {
        this.notFoundEvent = notFoundEvent;
    }

    public void setErrorOnLoadEvent(Event<ServerConfigurationErrorOnLoad> erroOnLoadEvent) {
        this.erroOnLoadEvent = erroOnLoadEvent;
    }

    protected void eventOnNotFound() {
        notFoundEvent.fire(new ServerConfigurationNotFound(getCollection(), path.toString()));
    }

    @Override
    public void eventErrorOnPropertiesLoad(IOException exception) {
        erroOnLoadEvent.fire(new ServerConfigurationErrorOnLoad(getCollection(), path.toString(), exception));
    }

    protected Path getPath() {
        return path;
    }

    protected void createPath() {
        String collection = getCollection();
        path = Paths.get(folder, collection + ".properties");
    }

    protected boolean pathExists() {
        return Files.exists(getPath());
    }

    @Override
    public InputStream propertiesInputStream() throws IOException {
        return new ByteArrayInputStream(Files.readAllBytes(getPath()));
    }

}
