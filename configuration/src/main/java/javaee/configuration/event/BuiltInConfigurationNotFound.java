package javaee.configuration.event;

public class BuiltInConfigurationNotFound {

    private Class<?> clazz;
    private String path;

    public BuiltInConfigurationNotFound(Class<?> clazz, String path) {
        super();
        this.clazz = clazz;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Class<?> getClazz() {
        return clazz;
    }

}
