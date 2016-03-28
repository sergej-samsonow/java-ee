package javaee.configuration.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ConfigurationCacheTest {

    private ConfigurationCache cache;

    private Map<String, String> data;

    @Before
    public void prepare() {
        data = new HashMap<>();
        data.put("A", "A-VALUE");
        data.put("B", "B-VALUE");
        cache = new ConfigurationCache();
    }

    @Test
    public void storeAndGet() throws Exception {
        cache.store("x", data);
        assertThat(cache.get("x"), equalTo(data));
    }

    @Test
    public void storeAndReturnStored() throws Exception {
        assertThat(cache.store("x", data), equalTo(data));
    }

    @Test
    public void storeImmutable() throws Exception {
        cache.store("x", data);
        data.remove("A");
        assertThat(cache.get("x").containsKey("A"), equalTo(true));
    }

    @Test
    public void getImmutable() throws Exception {
        cache.store("x", data);
        Map<String, String> stored = cache.get("x");
        stored.remove("A");
        assertThat(cache.get("x").containsKey("A"), equalTo(true));
    }

}
