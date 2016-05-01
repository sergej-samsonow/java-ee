package javaee.configuration.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Path;

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

import javaee.configuration.event.ServerConfigurationErrorOnLoad;
import javaee.configuration.event.ServerConfigurationNotFound;
import javaee.configuration.internal.ServerConfiguration;

@RunWith(MockitoJUnitRunner.class)
public class ServerConfigurationTest {

    @Spy
    @InjectMocks
    private ServerConfiguration process;

    @Mock
    private Event<ServerConfigurationNotFound> notFoundEvent;

    @Mock
    private Event<ServerConfigurationErrorOnLoad> errorOnLoadEvent;

    @Captor
    private ArgumentCaptor<ServerConfigurationNotFound> notFoundEventCaptor;

    @Captor
    private ArgumentCaptor<ServerConfigurationErrorOnLoad> errorOnLoadEventCaptor;

    @Mock
    private Path path;

    @Test
    public void eventOnNotFound() throws Exception {
        doReturn("C").when(process).getCollection();
        doReturn("P").when(path).toString();
        process.setNotFoundEvent(notFoundEvent);
        process.eventOnNotFound();
        verify(notFoundEvent).fire(notFoundEventCaptor.capture());
        assertThat(notFoundEventCaptor.getValue().getCollection(), equalTo("C"));
        assertThat(notFoundEventCaptor.getValue().getPath(), equalTo("P"));
    }

    @Test
    public void eventOnErrorOnLoad() throws Exception {
        doReturn("C").when(process).getCollection();
        doReturn("P").when(path).toString();

        IOException exception = new IOException("E");
        process.setErrorOnLoadEvent(errorOnLoadEvent);
        process.eventErrorOnPropertiesLoad(exception);

        verify(errorOnLoadEvent).fire(errorOnLoadEventCaptor.capture());
        assertThat(errorOnLoadEventCaptor.getValue().getCollection(), equalTo("C"));
        assertThat(errorOnLoadEventCaptor.getValue().getPath(), equalTo("P"));
        assertThat(errorOnLoadEventCaptor.getValue().getException(), equalTo(exception));
    }

    @Test
    public void prepare1() throws Exception {
        doNothing().when(process).createPath();
        doNothing().when(process).enable();
        doNothing().when(process).eventOnNotFound();
        doNothing().when(process).disable();
        doReturn(true).when(process).pathExists();

        process.prepare();

        InOrder order = inOrder(process);
        order.verify(process).createPath();
        order.verify(process).pathExists();
        order.verify(process).enable();
        order.verify(process, never()).eventOnNotFound();
        order.verify(process, never()).disable();
    }

    @Test
    public void prepare2() throws Exception {
        doNothing().when(process).createPath();
        doNothing().when(process).enable();
        doNothing().when(process).eventOnNotFound();
        doNothing().when(process).disable();
        doReturn(false).when(process).pathExists();

        process.prepare();

        InOrder order = inOrder(process);
        order.verify(process).createPath();
        order.verify(process).pathExists();
        order.verify(process, never()).enable();
        order.verify(process).eventOnNotFound();
        order.verify(process).disable();
    }

}
