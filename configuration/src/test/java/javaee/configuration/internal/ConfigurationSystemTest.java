package javaee.configuration.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentMap;

import javax.enterprise.event.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javaee.configuration.event.DatabaseConfigurationDisabled;
import javaee.configuration.event.ServerConfigurationIsDisabled;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationSystemTest {

    @Spy
    @InjectMocks
    private ConfigurationSystem system;

    @Mock
    private ConcurrentMap<String, ConfigurationCache> caches;

    @Mock
    private DirectoryObserver watcher;

    @Mock(name = "serverConfigurationIsDisabled")
    private Event<ServerConfigurationIsDisabled> serverConfigurationIsDisabled;

    @Mock(name = "databaseConfigurationIsDisabled")
    private Event<DatabaseConfigurationDisabled> databaseConfigurationIsDisabled;

    @Captor
    private ArgumentCaptor<ServerConfigurationIsDisabled> isDisabledCaptor;

    @Test
    public void configurationCache() throws Exception {
        when(caches.get("x")).thenReturn(new ConfigurationCache());
        assertThat(system.configurationCache("x"), instanceOf(ConfigurationCache.class));
        InOrder order = inOrder(caches);
        order.verify(caches).putIfAbsent(eq("x"), any(ConfigurationCache.class));
        order.verify(caches).get("x");
    }

    @Test
    public void init() throws Exception {
        doNothing().when(system).prepareDatabaseConfiguration();
        doNothing().when(system).prepareServerConfiguration();
        system.init();
        verify(system).prepareDatabaseConfiguration();
        verify(system).prepareServerConfiguration();
    }

    @Test
    public void shutdown() throws Exception {
        system.shutdown();
        verify(watcher).stop();
    }

    @Test
    public void proxy() throws Exception {
        assertThat(system.proxy(), instanceOf(CacheProxy.class));
    }

    @Test
    public void serverCacheProxy() throws Exception {
        CacheProxy proxy = mock(CacheProxy.class);
        ConfigurationCache cache = mock(ConfigurationCache.class);
        CacheableProcess process = mock(CacheableProcess.class);
        doReturn(proxy).when(system).proxy();
        doReturn(cache).when(system).configurationCache("server");
        assertThat(system.serverCacheProxy(process), equalTo(proxy));
        InOrder order = inOrder(proxy);
        order.verify(proxy).setCache(cache);
        order.verify(proxy).setConfigurationLoaderProcess(process);
    }

    @Test
    public void builtInCacheProxy() throws Exception {
        CacheProxy proxy = mock(CacheProxy.class);
        ConfigurationCache cache = mock(ConfigurationCache.class);
        CacheableProcess process = mock(CacheableProcess.class);
        doReturn(proxy).when(system).proxy();
        doReturn(cache).when(system).configurationCache("builtin");
        assertThat(system.builtInCacheProxy(process), equalTo(proxy));
        InOrder order = inOrder(proxy);
        order.verify(proxy).setCache(cache);
        order.verify(proxy).setConfigurationLoaderProcess(process);
    }

    @Test
    public void path1() throws Exception {
        doReturn(".").when(system).getServerConfigurationFolderPath();
        assertThat(system.path(), instanceOf(Path.class));
    }

    @Test
    public void path() throws Exception {
        doReturn(null).when(system).getServerConfigurationFolderPath();
        assertThat(system.path(), nullValue());
    }

    @Test
    public void prepareServerConfiguration() throws Exception {
        Path path = mock(Path.class);
        doReturn(path).when(system).path();
        doNothing().when(system).verifySeverConfiguration(path);
        doNothing().when(system).observeDirectory(path);
        system.prepareServerConfiguration();
        InOrder order = inOrder(system);
        order.verify(system).verifySeverConfiguration(path);
        order.verify(system).observeDirectory(path);
    }

    @Test
    public void isNotDirectory1() throws Exception {
        assertThat(system.isNotDirectory(null), equalTo(true));
    }

    @Test
    public void isNotDirectory2() throws Exception {
        assertThat(system.isNotDirectory(Paths.get("=$=")), equalTo(true));
    }

    @Test
    public void isNotDirectory3() throws Exception {
        assertThat(system.isNotDirectory(Paths.get(".")), equalTo(false));
    }

    @Test
    public void verifySeverConfiguration1() throws Exception {
        Path path = mock(Path.class);
        doReturn(true).when(system).isNotDirectory(path);
        system.verifySeverConfiguration(path);
        verify(serverConfigurationIsDisabled).fire(isDisabledCaptor.capture());
        assertThat(system.isServerConfigurationDisabled(), equalTo(true));
    }

    @Test
    public void verifySeverConfiguration2() throws Exception {
        Path path = mock(Path.class);
        doReturn(false).when(system).isNotDirectory(path);
        system.verifySeverConfiguration(path);
        verify(serverConfigurationIsDisabled, never()).fire(isDisabledCaptor.capture());
        assertThat(system.isServerConfigurationDisabled(), equalTo(false));
    }

    @Test
    public void startConfigurationWatcher1() throws Exception {
        Path path = mock(Path.class);
        ConfigurationCache cache = mock(ConfigurationCache.class);
        doReturn(false).when(system).isServerConfigurationDisabled();
        doReturn(cache).when(system).configurationCache("server");
        system.observeDirectory(path);
        verify(watcher).start(path, cache);
    }

    @Test
    public void startConfigurationWatcher2() throws Exception {
        Path path = mock(Path.class);
        ConfigurationCache cache = mock(ConfigurationCache.class);
        doReturn(true).when(system).isServerConfigurationDisabled();
        doReturn(cache).when(system).configurationCache("server");
        system.observeDirectory(path);
        verify(watcher, never()).start(path, cache);
    }

}
