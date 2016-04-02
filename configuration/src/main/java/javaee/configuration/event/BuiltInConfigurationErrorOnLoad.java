package javaee.configuration.event;

import java.io.IOException;

public class BuiltInConfigurationErrorOnLoad {

    private String collection;
    private Class<?> clazz;
    private String path;
    private IOException exception;

    public BuiltInConfigurationErrorOnLoad(String collection, Class<?> clazz, String path, IOException exception) {
        super();
        this.collection = collection;
        this.clazz = clazz;
        this.path = path;
        this.exception = exception;
    }

    public String getCollection() {
        return collection;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getPath() {
        return path;
    }

    public IOException getException() {
        return exception;
    }

}
