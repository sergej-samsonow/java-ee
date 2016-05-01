package javaee.configuration.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javaee.configuration.internal.CacheProxy;
import javaee.configuration.internal.CacheableProcess;
import javaee.configuration.internal.ConfigurationCache;

@RunWith(MockitoJUnitRunner.class)
public class CacheProxyTest {

    @Spy
    @InjectMocks
    private CacheProxy proxy;

    @Mock
    private ConfigurationCache cache;

    @Mock
    private CacheableProcess process;

    private String cacheId = "X";

    private Map<String, String> data;

    @Before
    public void prepareTest() throws Exception {
        data = map();
        when(process.cacheId()).thenReturn(cacheId);
        when(cache.get(cacheId)).thenReturn(data);
    }

    protected Map<String, String> map() {
        Map<String, String> map = new HashMap<>();
        map.put("A", "VAL");
        return map;
    }

    @Test
    public void setup() throws Exception {
        proxy = new CacheProxy();
        proxy.setCache(cache);
        proxy.setConfigurationLoaderProcess(process);
        proxy.prepare();
        verify(cache).get(cacheId);
        assertThat(proxy.data(), equalTo(data));
    }

    @Test
    public void load() throws Exception {
        Map<String, String> expected = map();
        doReturn(null).when(proxy).data();
        doNothing().when(proxy).store("x", expected);
        when(process.cacheId()).thenReturn("x");
        when(process.data()).thenReturn(expected);
        proxy.load();
        InOrder order = inOrder(process, proxy);
        order.verify(process).prepare();
        order.verify(process).load();
        order.verify(proxy).store("x", expected);
    }

    @Test
    public void load2() throws Exception {
        Map<String, String> expected = map();
        doReturn(expected).when(proxy).data();
        doNothing().when(proxy).store("x", expected);
        when(process.cacheId()).thenReturn("x");
        when(process.data()).thenReturn(expected);
        proxy.load();
        verify(process, never()).prepare();
        verify(process, never()).load();
        verify(proxy, never()).store("x", expected);
    }

    @Test
    public void store() throws Exception {
        Map<String, String> expected = map();
        when(cache.store("x", expected)).thenReturn(expected);
        proxy.store("x", expected);
        verify(cache).store("x", expected);
        assertThat(proxy.data(), CoreMatchers.equalTo(expected));
    }

}
