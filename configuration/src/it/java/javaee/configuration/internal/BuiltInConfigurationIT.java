package javaee.configuration.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BuiltInConfigurationIT {

    @InjectMocks
    private BuiltInConfiguration configuration;

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
        InputStream stream = getClass().getResourceAsStream("/valid.properties");
        Properties properties = configuration.properties(stream, "/valid.properties");
        assertThat(properties.getProperty("count"), equalTo("1"));
        assertThat(properties.getProperty("password"), equalTo("secret"));
    }

    @Test
    public void loadEmptyProperties() throws Exception {
        InputStream stream = getClass().getResourceAsStream("/empty.properties");
        Properties properties = configuration.properties(stream, "/empty.properties");
        assertThat(properties.stringPropertyNames().size(), equalTo(0));
    }

}
