package javaee.configuration.internal;

import java.util.Map;

public interface ConfigurationLoaderProcess {

    public void prepare();

    public void load();

    public Map<String, String> data();

}
