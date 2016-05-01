package javaee.configuration.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.enterprise.event.Event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javaee.configuration.event.BuiltInPropertiesError;
import javaee.configuration.internal.BuiltInConfiguration;
import javaee.configuration.event.BuiltInConfigurationNotFound;

@RunWith(MockitoJUnitRunner.class)
public class BuiltInConfigurationTest {

    @Spy
    @InjectMocks
    private BuiltInConfiguration process;

    @Mock
    private Event<BuiltInConfigurationNotFound> notFound;

    @Mock
    private Event<BuiltInPropertiesError> errorOnLoad;

    @Captor
    private ArgumentCaptor<BuiltInConfigurationNotFound> notFoundCaptor;

    @Captor
    private ArgumentCaptor<BuiltInPropertiesError> errorOnLoadCaptor;

    @Test
    public void prepare() throws Exception {
        doNothing().when(process).openStream();
        doNothing().when(process).enable();
        doReturn(false).when(process).streamIsEmpty();

        process.setNotFound(notFound);
        process.prepare();

        InOrder order = Mockito.inOrder(process);
        order.verify(process).openStream();
        order.verify(process).enable();
        order.verify(process).streamIsEmpty();
        verify(notFound, never()).fire(notFoundCaptor.capture());
        verify(process, never()).disable();
    }

    @Test
    public void prepare2() throws Exception {
        doNothing().when(process).openStream();
        doNothing().when(process).enable();
        doReturn(true).when(process).streamIsEmpty();

        doReturn("C").when(process).getCollection();
        doReturn(getClass()).when(process).getConfigurationFor();
        doReturn("P").when(process).getPath();

        process.setNotFound(notFound);
        process.prepare();

        InOrder order = inOrder(process, notFound);
        order.verify(process).openStream();
        order.verify(process).enable();
        order.verify(process).streamIsEmpty();
        order.verify(notFound).fire(notFoundCaptor.capture());
        order.verify(process).disable();

        assertThat(notFoundCaptor.getValue().getClazz(), equalTo(getClass()));
        assertThat(notFoundCaptor.getValue().getCollection(), equalTo("C"));
        assertThat(notFoundCaptor.getValue().getPath(), equalTo("P"));
    }

    @Test
    public void cacheId() throws Exception {
        doReturn(String.class).when(process).getConfigurationFor();
        doReturn("C").when(process).getCollection();
        assertThat(process.cacheId(), equalTo("java.lang.String.C"));
    }

    @Test
    public void eventErrorOnPropertiesLoad() throws Exception {
        doReturn("C").when(process).getCollection();
        doReturn(getClass()).when(process).getConfigurationFor();
        doReturn("P").when(process).getPath();
        IOException exception = new IOException("E");

        process.setErrorOnLoad(errorOnLoad);
        process.eventErrorOnPropertiesLoad(exception);

        verify(errorOnLoad).fire(errorOnLoadCaptor.capture());
        assertThat(errorOnLoadCaptor.getValue().getClazz(), equalTo(getClass()));
        assertThat(errorOnLoadCaptor.getValue().getCollection(), equalTo("C"));
        assertThat(errorOnLoadCaptor.getValue().getPath(), equalTo("P"));
        assertThat(errorOnLoadCaptor.getValue().getException(), equalTo(exception));
    }

    @Test
    public void getPath() throws Exception {
        doReturn("C").when(process).getCollection();
        assertThat(process.getPath(), equalTo("/C.properties"));
    }

    @Test
    public void streamIsEmptyByDefault() throws Exception {
        assertThat(process.streamIsEmpty(), equalTo(true));
    }

    @Test
    public void configurationFor() throws Exception {
        process.setConfigurationFor(String.class);
        assertThat(process.getConfigurationFor(), equalTo(String.class));
    }

}
