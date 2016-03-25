package javaee.configuration.event.builtinconfiguration;

public class PropertiesFileNotFound {

    private String path;

    public PropertiesFileNotFound(String path) {
        super();
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
