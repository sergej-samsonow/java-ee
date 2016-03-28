package javaee.configuration.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javaee.configuration.event.builtinconfiguration.ErrorOnPropertiesLoad;
import javaee.configuration.event.builtinconfiguration.PropertiesFileNotFound;

@RunWith(MockitoJUnitRunner.class)
public class BuiltInConfigurationTest {

    @Spy
    @InjectMocks
    private BuiltInConfiguration configuration;

    @Mock
    private ConfigurationCache cache;

    @Mock
    private InputStream stream;

    @Mock
    private Event<PropertiesFileNotFound> propertiesNotFound;

    @Mock
    private Event<ErrorOnPropertiesLoad> ioException;

    @Mock
    private Properties properties;

    private String id = "Unique.id";
    private String collection = "x";
    private String path = "/path";
    private Class<?> clazz = String.class;
    private Map<String, String> cachedResult = new HashMap<>();
    private Map<String, String> storedResult = new HashMap<>();

    @Before
    public void prepare() {
        cachedResult.put("cachedKey", "cachedValue");
        storedResult.put("storedKye", "storedValue");
        doReturn(properties).when(configuration).newProperties();
        doReturn(id).when(configuration).id(clazz, collection);
        doReturn(null).when(cache).get(id);
        doReturn(path).when(configuration).path(collection);
        doReturn(stream).when(configuration).stream(clazz, path);
        doReturn(storedResult).when(cache).store(any(), any());
        doReturn(new HashMap<>()).when(configuration).map(properties);
    }

    @Test
    public void id() throws Exception {
        assertThat(configuration.id(String.class, "data"), equalTo("java.lang.String.data"));
    }

    @Test
    public void path() throws Exception {
        assertThat(configuration.path("data"), equalTo("/data.properties"));
    }

    @Test
    public void newProperties() throws Exception {
        doCallRealMethod().when(configuration).newProperties();
        assertThat(configuration.newProperties(), instanceOf(Properties.class));
    }

    @Test
    public void properties() throws Exception {
        configuration.properties(stream, path);
        verify(properties).load(stream);
    }

    @Test
    public void propertiesCatchIoException() throws Exception {
        Properties properties = new Properties();
        properties.put("D", "M");
        properties = spy(properties);
        doReturn(properties).doReturn(properties).when(configuration).newProperties();
        doThrow(IOException.class).when(properties).load(stream);
        assertThat(configuration.properties(stream, path), equalTo(properties));
    }

    @Test
    public void propertiesFireErrorOnPropertiesLoad() throws Exception {
        IOException exception = spy(new IOException("X"));
        doReturn(properties).when(configuration).newProperties();
        doThrow(exception).when(properties).load(stream);
        ArgumentCaptor<ErrorOnPropertiesLoad> errorOnPropertiesLoad = ArgumentCaptor.forClass(ErrorOnPropertiesLoad.class);
        configuration.properties(stream, path);
        verify(ioException).fire(errorOnPropertiesLoad.capture());
        ErrorOnPropertiesLoad data = errorOnPropertiesLoad.getValue();
        assertThat(data.getException(), equalTo(exception));
        assertThat(data.getPath(), equalTo("/path"));
    }

    @Test
    public void map() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("first", "a");
        properties.setProperty("second", "b");
        Map<String, String> map = configuration.map(properties);
        assertThat(map.get("first"), equalTo("a"));
        assertThat(map.get("second"), equalTo("b"));
    }

    @Test
    public void dataClazzParameterNull() throws Exception {
        Map<String, String> result = configuration.data(null, "collection");
        assertThat(result.size(), equalTo(0));
    }

    @Test
    public void dataCollectionParameterNull() throws Exception {
        Map<String, String> result = configuration.data(clazz, null);
        assertThat(result.size(), equalTo(0));
    }

    @Test
    public void dataCacheCollectionReturnNull() throws Exception {
        doReturn(null).when(cache).get(id);
        configuration.data(clazz, collection);
        verify(cache).get(id);
        verify(configuration).path(collection);
    }

    @Test
    public void dataCacheCollectionReturnCollection() throws Exception {
        doReturn(cachedResult).when(cache).get(id);
        assertThat(configuration.data(clazz, collection), equalTo(cachedResult));
        verify(cache).get(id);
        verify(configuration, never()).path(collection);
    }

    @Test
    public void dataStreamReturnObject() throws Exception {
        configuration.data(clazz, collection);
        verify(configuration).stream(clazz, path);
        verify(configuration).properties(stream, path);
    }

    @Test
    public void dataStreamReturnNull() throws Exception {
        doReturn(null).when(configuration).stream(clazz, path);
        configuration.data(clazz, collection);
        verify(configuration).stream(clazz, path);
        verify(configuration, never()).properties(any(), any());
    }

    @Test
    public void dataStreamFirePropertiesFileNotFound() throws Exception {
        doReturn(null).when(configuration).stream(clazz, path);
        ArgumentCaptor<PropertiesFileNotFound> captor = ArgumentCaptor.forClass(PropertiesFileNotFound.class);
        configuration.data(clazz, collection);
        verify(propertiesNotFound).fire(captor.capture());
        assertThat(captor.getValue().getPath(), equalTo(path));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void dataStreamStoreInvoked() throws Exception {
        Map<String, String> map = new HashMap<>();
        doReturn(null).when(configuration).stream(clazz, path);
        configuration.data(clazz, collection);
        verify(cache).store(any(String.class), any(map.getClass()));
    }

    @Test
    public void dataStreamReturnStoredCollection() throws Exception {
        doReturn(null).when(configuration).stream(clazz, path);
        assertThat(configuration.data(clazz, collection), equalTo(storedResult));
    }

    @Test
    public void dataMapInvoked() throws Exception {
        configuration.data(clazz, collection);
        verify(configuration).map(properties);
    }

    @Test
    public void dataStoreInvoked() throws Exception {
        assertThat(configuration.data(clazz, collection), equalTo(storedResult));
        verify(cache).store(any(), any());
    }

}
