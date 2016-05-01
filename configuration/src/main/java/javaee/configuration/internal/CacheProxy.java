package javaee.configuration.internal;

import java.util.Map;

public class CacheProxy implements ConfigurationLoaderProcess {

    private ConfigurationCache cache;
    private CacheableProcess process;
    private Map<String, String> data;

    public void setCache(ConfigurationCache cache) {
        this.cache = cache;
    }

    public void setConfigurationLoaderProcess(CacheableProcess process) {
        this.process = process;
    }

    @Override
    public void prepare() {
        data = cache.get(process.cacheId());
    }

    @Override
    public void load() {
        if (data() == null) {
            process.prepare();
            process.load();
            store(process.cacheId(), process.data());
        }
    }

    protected void store(String cacheId, Map<String, String> processed) {
        data = cache.store(cacheId, processed);
    }

    @Override
    public Map<String, String> data() {
        return data;
    }

}
