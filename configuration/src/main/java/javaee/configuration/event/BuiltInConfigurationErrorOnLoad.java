package javaee.configuration.event;

import java.io.IOException;

public class BuiltInConfigurationErrorOnLoad {

    private String path;
    private IOException exception;

    public BuiltInConfigurationErrorOnLoad(String path, IOException exception) {
        super();
        this.path = path;
        this.exception = exception;
    }

    public String getPath() {
        return path;
    }

    public IOException getException() {
        return exception;
    }

}
