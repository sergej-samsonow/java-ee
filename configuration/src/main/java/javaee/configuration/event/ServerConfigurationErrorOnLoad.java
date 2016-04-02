package javaee.configuration.event;

import java.io.IOException;

public class ServerConfigurationErrorOnLoad {

    private String collection;
    private String path;
    private IOException exception;

    public ServerConfigurationErrorOnLoad(String collection, String path, IOException exception) {
        super();
        this.collection = collection;
        this.path = path;
        this.exception = exception;
    }

    public String getCollection() {
        return collection;
    }

    public String getPath() {
        return path;
    }

    public IOException getException() {
        return exception;
    }

}
