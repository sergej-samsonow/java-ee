package javaee.configuration.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class PropertiesLoaderProcess implements CacheableProcess {

    private String collection;
    private Map<String, String> data;
    private boolean enabled = false;

    protected void disable() {
        enabled = false;
    }

    protected void enable() {
        enabled = true;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    protected String getCollection() {
        return this.collection;
    }

    protected Properties properties() {
        return new Properties();
    }

    protected void read() {
        data = new HashMap<>();
        try {
            InputStream stream = propertiesInputStream();
            Properties properties = properties();
            properties.load(stream);
            stream.close();
            for (String key : properties.stringPropertyNames()) {
                data.put(key, properties.getProperty(key));
            }
        }
        catch (IOException exception) {
            eventErrorOnPropertiesLoad(exception);
        }
    }

    abstract public InputStream propertiesInputStream() throws IOException;

    abstract public void eventErrorOnPropertiesLoad(IOException exception);

    @Override
    public void load() {
        if (enabled) {
            read();
        }
    }

    @Override
    public Map<String, String> data() {
        return data;
    }

    @Override
    public String cacheId() {
        return collection;
    }

}
