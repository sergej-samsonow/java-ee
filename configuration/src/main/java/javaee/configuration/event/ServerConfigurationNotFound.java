package javaee.configuration.event;

public class ServerConfigurationNotFound {

    private String collection;
    private String path;

    public ServerConfigurationNotFound(String collection, String path) {
        super();
        this.collection = collection;
        this.path = path;
    }

    public String getCollection() {
        return collection;
    }

    public String getPath() {
        return path;
    }

}
