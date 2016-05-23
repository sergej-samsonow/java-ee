package javaee.configuration.internal;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import javaee.configuration.event.DirectoryObservationError;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class DirectoryObserver {

    private Path folder;
    private AtomicBoolean started = new AtomicBoolean(false);
    private AtomicBoolean stoped = new AtomicBoolean(false);
    private ConfigurationCache cache;
    private WatchKey key;
    private WatchService watcher;

    @Inject
    private Event<DirectoryObservationError> directoryObservationError;

    /**
     * Start directory observation. Transaction type not supported to avoid
     * transaction timeout.
     *
     * @param folder
     * @param cache
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void start(Path folder, ConfigurationCache cache) {
        if (started.getAndSet(true)) {
            return;
        }
        this.folder = folder;
        this.cache = cache;
        watch();

    }

    protected void watch() {
        start();
        while (!stoped.get()) {
            next();
        }
    }

    protected void next() {
        try {
            key = watcher.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                cleanCache(event);
            }
            key.reset();
        }
        catch (Exception exception) {
            directoryObservationError.fire(new DirectoryObservationError(folder.toString(), exception));
        }
    }

    @SuppressWarnings("unchecked")
    protected void cleanCache(WatchEvent<?> event) {
        if (event.kind() != OVERFLOW) {
            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            Path context = ev.context();
            Path name = context.getFileName();
            String key = name.toString().replaceAll("(.*)(\\..*?)$", "$1");
            cache.delete(key);
        }
    }

    protected void start() {
        try {
            init();
            folder.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            stoped.set(false);
        }
        catch (Exception exception) {
            stoped.set(true);
            directoryObservationError.fire(new DirectoryObservationError(folder.toString(), exception));
        }
    }

    protected void init() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
    }

    public void stop() {
        try {
            stoped.set(true);
            started.set(false);
            watcher.close();
        }
        catch (Exception exception) {
        }
    }
}
