package javaee.configuration.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javaee.configuration.Collection;
import javaee.configuration.Configuration;
import javaee.configuration.event.BuiltInPropertiesError;
import javaee.configuration.event.BuiltInConfigurationNotFound;
import javaee.configuration.event.ServerConfigurationErrorOnLoad;
import javaee.configuration.event.ServerConfigurationNotFound;
import javaee.configuration.internal.BuiltInConfiguration;
import javaee.configuration.internal.CacheProxy;
import javaee.configuration.internal.ConfigurationLoaderProcess;
import javaee.configuration.internal.ConfigurationProducer;
import javaee.configuration.internal.ConfigurationSystem;
import javaee.configuration.internal.ServerConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationProducerTest {

    @Spy
    @InjectMocks
    private ConfigurationProducer producer;

    @Mock
    private InjectionPoint ip;

    @Mock
    private Collection collection;

    @Mock
    private Annotated annotated;

    @Mock
    private Configuration annotation;

    @Mock
    private Bean<?> bean;

    @Mock
    private BuiltInConfiguration builtInProcess;

    @Mock
    private ServerConfiguration serverProcess;

    @Mock
    private ConfigurationSystem system;

    @Mock
    private Event<BuiltInConfigurationNotFound> builtInConfigurationNotFound;

    @Mock
    private Event<BuiltInPropertiesError> builtInPropertiesError;

    @Mock
    private Event<ServerConfigurationNotFound> serverConfigurationNotFound;

    @Mock
    private Event<ServerConfigurationErrorOnLoad> serverConfiguraionErrorOnLoad;

    @Mock
    private CacheProxy proxy;

    @Test
    public void collection() throws Exception {
        doNothing().when(producer).init();
        doNothing().when(producer).procesInjectionPoint(ip);
        doNothing().when(producer).loadBuiltInConfiguration();
        doNothing().when(producer).loadDatabaseConfigration();
        doNothing().when(producer).loadServerConfiguration();
        doNothing().when(producer).clean();
        doReturn(collection).when(producer).createObject();
        assertThat(producer.collection(ip), equalTo(collection));
        InOrder order = inOrder(producer);
        order.verify(producer).init();
        order.verify(producer).procesInjectionPoint(ip);
        order.verify(producer).loadBuiltInConfiguration();
        order.verify(producer).loadDatabaseConfigration();
        order.verify(producer).loadServerConfiguration();
        order.verify(producer).createObject();
        order.verify(producer).clean();

    }

    @Test
    public void init() throws Exception {
        producer.init();
        assertThat(producer.data().isEmpty(), equalTo(true));
        assertThat(producer.collection(), nullValue());
        assertThat(producer.clazz(), nullValue());
    }

    @Test
    public void procesInjectionPoint1() throws Exception {
        when(ip.getAnnotated()).thenReturn(annotated);
        when(annotated.getAnnotation(Configuration.class)).thenReturn(annotation);
        when(annotation.value()).thenReturn("x");
        doReturn(bean).when(ip).getBean();
        doReturn(String.class).when(bean).getBeanClass();
        producer.procesInjectionPoint(ip);
        assertThat(producer.collection(), equalTo("x"));
        assertThat(producer.clazz(), equalTo(String.class));
    }

    @Test
    public void procesInjectionPoint2() throws Exception {
        when(ip.getAnnotated()).thenReturn(annotated);
        when(annotated.getAnnotation(Configuration.class)).thenReturn(annotation);
        when(annotation.value()).thenReturn(null);
        doReturn(bean).when(ip).getBean();
        doReturn(String.class).when(bean).getBeanClass();
        producer.procesInjectionPoint(ip);
        assertThat(producer.collection(), nullValue());
        assertThat(producer.clazz(), nullValue());
    }

    @Test
    public void procesInjectionPoint3() throws Exception {
        when(ip.getAnnotated()).thenReturn(annotated);
        when(annotated.getAnnotation(Configuration.class)).thenReturn(annotation);
        when(annotation.value()).thenReturn("");
        doReturn(bean).when(ip).getBean();
        doReturn(String.class).when(bean).getBeanClass();
        producer.procesInjectionPoint(ip);
        assertThat(producer.collection(), nullValue());
        assertThat(producer.clazz(), nullValue());
    }

    @Test
    public void loadBuiltInConfiguration1() throws Exception {
        doReturn("x").when(producer).collection();
        doReturn(String.class).when(producer).clazz();
        doReturn(builtInProcess).when(producer).builtInProcess();
        doReturn(proxy).when(system).builtInCacheProxy(builtInProcess);
        doNothing().when(producer).execute(proxy);
        producer.loadBuiltInConfiguration();
        InOrder order = inOrder(producer, builtInProcess);
        order.verify(builtInProcess).setConfigurationFor(String.class);
        order.verify(builtInProcess).setCollection("x");
        order.verify(builtInProcess).setNotFound(builtInConfigurationNotFound);
        order.verify(builtInProcess).setErrorOnLoad(builtInPropertiesError);
        order.verify(producer).execute(proxy);

    }

    @Test
    public void loadBuiltInConfiguration2() throws Exception {
        doReturn(null).when(producer).collection();
        doReturn(String.class).when(producer).clazz();
        doReturn(builtInProcess).when(producer).builtInProcess();
        doReturn(proxy).when(system).builtInCacheProxy(builtInProcess);
        doNothing().when(producer).execute(proxy);
        producer.loadBuiltInConfiguration();
        verify(builtInProcess, never()).setConfigurationFor(any());
        verify(builtInProcess, never()).setCollection(any());
        verify(builtInProcess, never()).setNotFound(any());
        verify(builtInProcess, never()).setErrorOnLoad(any());
        verify(producer, never()).execute(any());
    }

    @Test
    public void loadBuiltInConfiguration3() throws Exception {
        doReturn("x").when(producer).collection();
        doReturn(null).when(producer).clazz();
        doReturn(builtInProcess).when(producer).builtInProcess();
        doReturn(proxy).when(system).builtInCacheProxy(builtInProcess);
        doNothing().when(producer).execute(proxy);
        producer.loadBuiltInConfiguration();
        verify(builtInProcess, never()).setConfigurationFor(any());
        verify(builtInProcess, never()).setCollection(any());
        verify(builtInProcess, never()).setNotFound(any());
        verify(builtInProcess, never()).setErrorOnLoad(any());
        verify(producer, never()).execute(any());
    }

    @Test
    public void loadServerConfiguration1() throws Exception {
        doReturn("x").when(producer).collection();
        doReturn(false).when(system).isServerConfigurationDisabled();
        doReturn("F").when(system).getServerConfigurationFolderPath();
        doReturn(serverProcess).when(producer).serverProcess();
        doReturn(proxy).when(system).serverCacheProxy(serverProcess);
        doNothing().when(producer).execute(proxy);
        producer.loadServerConfiguration();
        InOrder order = inOrder(producer, serverProcess);
        order.verify(serverProcess).setFolder("F");
        order.verify(serverProcess).setCollection("x");
        order.verify(serverProcess).setNotFoundEvent(serverConfigurationNotFound);
        order.verify(serverProcess).setErrorOnLoadEvent(serverConfiguraionErrorOnLoad);
        order.verify(producer).execute(proxy);
    }

    @Test
    public void loadServerConfiguration2() throws Exception {
        doReturn(null).when(producer).collection();
        doReturn(false).when(system).isServerConfigurationDisabled();
        doReturn("F").when(system).getServerConfigurationFolderPath();
        doReturn(serverProcess).when(producer).serverProcess();
        doReturn(proxy).when(system).serverCacheProxy(serverProcess);
        doNothing().when(producer).execute(proxy);
        producer.loadServerConfiguration();
        verify(serverProcess, never()).setFolder("F");
        verify(serverProcess, never()).setCollection("x");
        verify(serverProcess, never()).setNotFoundEvent(serverConfigurationNotFound);
        verify(serverProcess, never()).setErrorOnLoadEvent(serverConfiguraionErrorOnLoad);
        verify(producer, never()).execute(proxy);
    }

    @Test
    public void loadServerConfiguration3() throws Exception {
        doReturn("x").when(producer).collection();
        doReturn(true).when(system).isServerConfigurationDisabled();
        doReturn("F").when(system).getServerConfigurationFolderPath();
        doReturn(serverProcess).when(producer).serverProcess();
        doReturn(proxy).when(system).serverCacheProxy(serverProcess);
        doNothing().when(producer).execute(proxy);
        producer.loadServerConfiguration();
        verify(serverProcess, never()).setFolder("F");
        verify(serverProcess, never()).setCollection("x");
        verify(serverProcess, never()).setNotFoundEvent(serverConfigurationNotFound);
        verify(serverProcess, never()).setErrorOnLoadEvent(serverConfiguraionErrorOnLoad);
        verify(producer, never()).execute(proxy);
    }

    @Test
    public void execute() throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("x", "b");
        ConfigurationLoaderProcess process = mock(ConfigurationLoaderProcess.class);
        doReturn(data).when(process).data();
        doNothing().when(producer).merge(data);
        producer.execute(process);
        InOrder order = inOrder(producer, process);
        order.verify(process).prepare();
        order.verify(process).load();
        order.verify(producer).merge(data);
    }

    @Test
    public void createObject() throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put("x", "b");
        doReturn("B").when(producer).collection();
        doReturn(data).when(producer).data();
        Collection collected = producer.createObject();
        assertThat(collected.getName(), equalTo("B"));
        assertThat(collected.getData(), equalTo(data));
    }

    @Test
    public void builtInProcess() throws Exception {
        assertThat(producer.builtInProcess(), instanceOf(BuiltInConfiguration.class));
    }

    @Test
    public void serverProcess() throws Exception {
        assertThat(producer.serverProcess(), instanceOf(ServerConfiguration.class));
    }

    @Test
    public void clean() throws Exception {
        producer.clean();
        assertThat(producer.collection(), nullValue());
        assertThat(producer.clazz(), nullValue());
        assertThat(producer.data(), nullValue());
    }

}
