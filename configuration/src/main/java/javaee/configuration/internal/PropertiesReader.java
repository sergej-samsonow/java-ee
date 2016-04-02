package javaee.configuration.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesReader {

    public Map<String, String> read(InputStream stream) throws IOException {
        Properties properties = properties();
        properties.load(stream);
        stream.close();
        return map(properties);
    }

    protected Properties properties() {
        return new Properties();
    }

    protected Map<String, String> map(Properties properties) {
        HashMap<String, String> map = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            map.put(key, properties.getProperty(key));
        }
        return map;
    }
}
