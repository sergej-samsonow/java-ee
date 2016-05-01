package javaee.configuration.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesLoaderProcessTest {

    @Spy
    @InjectMocks
    private GeneralPropertiesConfigurationLoaderProcessWrapper wrapper;

    @Mock
    private InputStream stream;

    private Map<String, String> expected;

    @Spy
    private Properties properties;

    @Mock
    private IOException exception;

    @Before
    public void prepare() throws IOException {
        properties.put("a.key", "a.value");
        properties.put("b.key", "b.value");

        expected = new HashMap<>();
        expected.put("a.key", "a.value");
        expected.put("b.key", "b.value");
        doReturn(properties).when(wrapper).properties();
        doReturn(stream).when(wrapper).propertiesInputStream();
    }

    @Test
    public void read() throws Exception {
        wrapper.read();
        assertThat(wrapper.data(), equalTo(expected));
    }

    @Test
    public void readWithIOException() throws Exception {
        doThrow(exception).when(properties).load(stream);
        wrapper.read();
        assertThat(wrapper.data(), equalTo(new HashMap<>()));
        verify(wrapper).eventErrorOnPropertiesLoad(exception);
    }

    @Test
    public void loadEnabled() throws Exception {
        doReturn(stream).when(wrapper).propertiesInputStream();
        doNothing().when(wrapper).read();
        wrapper.enable();
        wrapper.load();
        verify(wrapper).read();
    }

    @Test
    public void loadDisabled() throws Exception {
        wrapper.disable();
        wrapper.load();
        verify(wrapper, never()).propertiesInputStream();
    }

    @Test
    public void collection() throws Exception {
        wrapper.setCollection("x");
        assertThat(wrapper.getCollection(), equalTo("x"));
        assertThat(wrapper.cacheId(), equalTo("x"));
    }

    @Test
    public void properties() throws Exception {
        wrapper = new GeneralPropertiesConfigurationLoaderProcessWrapper();
        assertThat(wrapper.properties(), instanceOf(Properties.class));
    }

}
