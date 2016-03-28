package javaee.configuration.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PropertiesReaderTest {

    private static final String VALUE = "A-VALUE";

    private static final String KEY = "A";

    @Spy
    private PropertiesReader reader;

    @Mock
    private Properties properties;

    @Mock
    private InputStream stream;

    private Properties expectedProperties() {
        Properties properties = new Properties();
        properties.setProperty(KEY, VALUE);
        return properties;
    }

    private Map<String, String> expectedMap() {
        Map<String, String> map = new HashMap<>();
        map.put(KEY, VALUE);
        return map;
    }

    @Before
    public void prepare() {
        doReturn(properties).when(reader).properties();
        doReturn(expectedMap()).when(reader).map(properties);
    }

    @Test
    public void properties() throws Exception {
        doCallRealMethod().when(reader).properties();
        assertThat(reader.properties(), instanceOf(Properties.class));
    }

    @Test
    public void map() throws Exception {
        doCallRealMethod().when(reader).map(expectedProperties());
        assertThat(reader.map(expectedProperties()), equalTo(expectedMap()));
    }

    @Test
    public void readInOrder() throws Exception {
        reader.read(stream);
        InOrder order = inOrder(reader, properties);
        order.verify(reader).properties();
        order.verify(properties).load(stream);
        order.verify(reader).map(properties);
    }

    @Test
    public void readReturnValue() throws Exception {
        assertThat(reader.read(stream), equalTo(expectedMap()));
    }

    @Test(expected = IOException.class)
    public void forwardExceptionOnRead() throws Exception {
        doThrow(new IOException()).when(properties).load(stream);
        reader.read(stream);
    }

}
