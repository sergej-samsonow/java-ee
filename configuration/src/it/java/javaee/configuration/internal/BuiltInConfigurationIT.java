package javaee.configuration.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.enterprise.event.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javaee.configuration.event.builtinconfiguration.ErrorOnPropertiesLoad;
import javaee.configuration.event.builtinconfiguration.PropertiesFileNotFound;

@RunWith(MockitoJUnitRunner.class)
public class BuiltInConfigurationIT {

    @InjectMocks
    private BuiltInConfiguration configuration;

    @Mock
    private Event<PropertiesFileNotFound> propertiesNotFound;

    @Mock
    private Event<ErrorOnPropertiesLoad> ioException;

    @Test
    public void stream() throws Exception {
        InputStream stream = configuration.stream(getClass(), "/it-text.txt");
        String line = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            line = reader.readLine();
        }
        assertThat(line, equalTo("OK"));
    }

    @Test
    public void loadValidProperties() throws Exception {
        Map<String, String> map = configuration.data(getClass(), "valid");
        verify(propertiesNotFound, never()).fire(any());
        assertThat(map.get("count"), equalTo("1"));
        assertThat(map.get("password"), equalTo("secret"));
    }

    @Test
    public void loadEmptyProperties() throws Exception {
        Map<String, String> map = configuration.data(BuiltInConfiguration.class, "empty");
        assertThat(map.size(), equalTo(0));
    }

}
