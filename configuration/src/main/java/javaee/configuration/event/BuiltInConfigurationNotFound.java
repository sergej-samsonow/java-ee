package javaee.configuration.event;

public class BuiltInConfigurationNotFound {

    private String path;

    public BuiltInConfigurationNotFound(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
