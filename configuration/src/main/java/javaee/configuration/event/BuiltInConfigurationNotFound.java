package javaee.configuration.event;

public class BuiltInConfigurationNotFound {

    private String collection;
    private Class<?> clazz;
    private String path;

    public BuiltInConfigurationNotFound(String collection, Class<?> clazz, String path) {
        super();
        this.collection = collection;
        this.clazz = clazz;
        this.path = path;
    }

    public String getCollection() {
        return collection;
    }

    public String getPath() {
        return path;
    }

    public Class<?> getClazz() {
        return clazz;
    }

}
