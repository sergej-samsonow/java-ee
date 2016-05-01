package javaee.configuration.internal;

import static java.nio.file.Files.isDirectory;
import static javax.ejb.ConcurrencyManagementType.CONTAINER;
import static javax.ejb.LockType.READ;
import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.Lock;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import javaee.configuration.event.DatabaseConfigurationDisabled;
import javaee.configuration.event.ServerConfigurationIsDisabled;

@Singleton
@ConcurrencyManagement(CONTAINER)
@TransactionAttribute(NOT_SUPPORTED)
@Lock(READ)
public class ConfigurationSystem {

    private ConcurrentMap<String, ConfigurationCache> caches = new ConcurrentHashMap<>();

    private boolean serverConfigurationDisabled;
    private boolean databaseConfigurationDisabled;

    @Resource(mappedName = "java:comp/env/configuration")
    private String folder;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Event<ServerConfigurationIsDisabled> serverConfigurationIsDisabled;

    @Inject
    private Event<DatabaseConfigurationDisabled> databaseConfigurationIsDisabled;

    @Inject
    private DirectoryObserver watcher;

    @PostConstruct
    protected void init() {
        prepareDatabaseConfiguration();
        prepareServerConfiguration();
    }

    @PreDestroy
    protected void shutdown() {
        watcher.stop();
    }

    protected ConfigurationCache configurationCache(String system) {
        caches.putIfAbsent(system, new ConfigurationCache());
        return caches.get(system);
    }

    protected CacheProxy proxy() {
        return new CacheProxy();
    }

    public CacheProxy serverCacheProxy(CacheableProcess process) {
        CacheProxy proxy = proxy();
        proxy.setCache(configurationCache("server"));
        proxy.setConfigurationLoaderProcess(process);
        return proxy;
    }

    public CacheProxy builtInCacheProxy(CacheableProcess process) {
        CacheProxy proxy = proxy();
        proxy.setCache(configurationCache("builtin"));
        proxy.setConfigurationLoaderProcess(process);
        return proxy;
    }

    protected Path path() {
        try {
            return Paths.get(getServerConfigurationFolderPath());
        }
        catch (Exception exception) {
            return null;
        }
    }

    protected void prepareServerConfiguration() {
        Path root = path();
        verifySeverConfiguration(root);
        observeDirectory(root);
    }

    protected boolean isNotDirectory(Path root) {
        return root == null || !isDirectory(root);
    }

    protected void verifySeverConfiguration(Path root) {
        serverConfigurationDisabled = isNotDirectory(root);
        if (isServerConfigurationDisabled()) {
            serverConfigurationIsDisabled.fire(new ServerConfigurationIsDisabled());
        }
    }

    public boolean isServerConfigurationDisabled() {
        return serverConfigurationDisabled;
    }

    protected void observeDirectory(Path root) {
        if (!isServerConfigurationDisabled()) {
            watcher.start(root, configurationCache("server"));
        }
    }

    public String getServerConfigurationFolderPath() {
        return folder;
    }

    public boolean isDatabaseConfigurationDisabled() {
        return databaseConfigurationDisabled;
    }

    protected void prepareDatabaseConfiguration() {
        databaseConfigurationDisabled = true;
        if (em != null) {
            try {
                TypedQuery<ConfigurationEntity> query = em.createQuery("select ce from ConfigurationEntity as ce", ConfigurationEntity.class);
                query.setMaxResults(1);
                query.getResultList();
                databaseConfigurationDisabled = false;
            }
            catch (Exception e) {
                databaseConfigurationIsDisabled.fire(new DatabaseConfigurationDisabled());
            }
        }
    }

}
