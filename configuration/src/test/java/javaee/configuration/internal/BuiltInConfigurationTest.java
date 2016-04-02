package javaee.configuration.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javaee.configuration.event.BuiltInConfigurationErrorOnLoad;
import javaee.configuration.event.BuiltInConfigurationNotFound;

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
    private PropertiesReader reader;

    @Mock
    private Event<BuiltInConfigurationNotFound> notFound;

    @Mock
    private Event<BuiltInConfigurationErrorOnLoad> errorOnLoad;

    private String id = "Unique.id";
    private String collection = "x";
    private String path = "/path";
    private Class<?> clazz = String.class;
    private Map<String, String> cachedResult = new HashMap<>();
    private Map<String, String> storedResult = new HashMap<>();

    @Before
    public void prepare() throws IOException {
        cachedResult.put("cachedKey", "cachedValue");
        storedResult.put("storedKye", "storedValue");
        doReturn(id).when(configuration).id(clazz, collection);
        doReturn(null).when(cache).get(id);
        doReturn(path).when(configuration).path(collection);
        doReturn(stream).when(configuration).stream(clazz, path);
        doReturn(storedResult).when(cache).store(any(), any());
        doReturn(new HashMap<>()).when(reader).read(stream);
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
        verify(reader).read(stream);
    }

    @Test
    public void dataStreamReturnNull() throws Exception {
        doReturn(null).when(configuration).stream(clazz, path);
        configuration.data(clazz, collection);
        verify(configuration).stream(clazz, path);
        verify(reader, never()).read(any());
    }

    @Test
    public void dataStreamFirePropertiesFileNotFound() throws Exception {
        doReturn(null).when(configuration).stream(clazz, path);
        ArgumentCaptor<BuiltInConfigurationNotFound> captor = ArgumentCaptor.forClass(BuiltInConfigurationNotFound.class);
        configuration.data(clazz, collection);
        verify(notFound).fire(captor.capture());
        assertThat(captor.getValue().getCollection(), equalTo(collection));
        assertThat(captor.getValue().getPath(), equalTo(path));
        assertThat(captor.getValue().getClazz(), equalTo(clazz));
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
    public void dataIoExceptionEvent() throws Exception {
        IOException exception = spy(new IOException("X"));
        doThrow(exception).when(reader).read(stream);
        ArgumentCaptor<BuiltInConfigurationErrorOnLoad> builtInConfigurationErrorOnLoad = ArgumentCaptor.forClass(BuiltInConfigurationErrorOnLoad.class);
        configuration.data(clazz, collection);
        verify(errorOnLoad).fire(builtInConfigurationErrorOnLoad.capture());
        BuiltInConfigurationErrorOnLoad data = builtInConfigurationErrorOnLoad.getValue();
        assertThat(data.getCollection(), equalTo(collection));
        assertThat(data.getClazz(), equalTo(clazz));
        assertThat(data.getException(), equalTo(exception));
        assertThat(data.getPath(), equalTo("/path"));
    }

}
