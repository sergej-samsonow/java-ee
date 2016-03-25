package javaee.configuration.event.builtinconfiguration;

import java.io.IOException;

public class ErrorOnPropertiesLoad {

    private String path;
    private IOException exception;

    public ErrorOnPropertiesLoad(String path, IOException exception) {
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
