package javaee.configuration.internal;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import javaee.configuration.Collection;
import javaee.configuration.Configuration;
import javaee.configuration.event.BuiltInConfigurationNotFound;
import javaee.configuration.event.BuiltInPropertiesError;
import javaee.configuration.event.ServerConfigurationErrorOnLoad;
import javaee.configuration.event.ServerConfigurationNotFound;

@Stateless
@NamedQuery(name = "collection", query = ""
        + "select ce from ConfigurationEntity as ce"
        + "    where ce.collection = :collection")
public class ConfigurationProducer {

    @Inject
    private ConfigurationSystem system;

    @Inject
    private Event<BuiltInConfigurationNotFound> builtInConfigurationNotFound;

    @Inject
    private Event<BuiltInPropertiesError> builtInPropertiesError;

    @Inject
    private Event<ServerConfigurationNotFound> serverConfigurationNotFound;

    @Inject
    private Event<ServerConfigurationErrorOnLoad> serverConfiguraionErrorOnLoad;

    @PersistenceContext
    private EntityManager em;

    private String collection;
    private Class<?> clazz;
    private Map<String, String> data;

    @Produces
    @Configuration
    public Collection collection(InjectionPoint ip) {
        init();
        procesInjectionPoint(ip);
        loadBuiltInConfiguration();
        loadDatabaseConfigration();
        loadServerConfiguration();
        Collection configuration = createObject();
        clean();
        return configuration;
    }

    protected void init() {
        data = new HashMap<>();
        clazz = null;
        collection = null;
    }

    protected void procesInjectionPoint(InjectionPoint ip) {
        collection = ip.getAnnotated().getAnnotation(Configuration.class).value();
        if (collection == null || collection.isEmpty()) {
            collection = null;
            return;
        }
        clazz = ip.getBean().getBeanClass();
    }

    protected void loadBuiltInConfiguration() {
        if (collection() == null || clazz() == null) {
            return;
        }
        BuiltInConfiguration process = builtInProcess();
        process.setConfigurationFor(clazz());
        process.setCollection(collection());
        process.setNotFound(builtInConfigurationNotFound);
        process.setErrorOnLoad(builtInPropertiesError);
        execute(system.builtInCacheProxy(process));
    }

    protected void loadDatabaseConfigration() {
        if (collection() == null || system.isDatabaseConfigurationDisabled()) {
            return;
        }
        TypedQuery<ConfigurationEntity> query = em.createNamedQuery("collection", ConfigurationEntity.class);
        query.setParameter("collection", collection());
        HashMap<String, String> data = new HashMap<>();
        for (ConfigurationEntity current : query.getResultList()) {
            data.put(current.getKey(), current.getValue());
        }
        merge(data);
    }

    protected void loadServerConfiguration() {
        if (collection() == null || system.isServerConfigurationDisabled()) {
            return;
        }
        ServerConfiguration process = serverProcess();
        process.setFolder(system.getServerConfigurationFolderPath());
        process.setCollection(collection());
        process.setNotFoundEvent(serverConfigurationNotFound);
        process.setErrorOnLoadEvent(serverConfiguraionErrorOnLoad);
        execute(system.serverCacheProxy(process));
    }

    public void execute(ConfigurationLoaderProcess process) {
        process.prepare();
        process.load();
        merge(process.data());
    }

    protected String collection() {
        return collection;
    }

    protected Class<?> clazz() {
        return clazz;
    }

    protected void merge(Map<String, String> incomming) {
        data().putAll(incomming);
    }

    protected Map<String, String> data() {
        return data;
    }

    protected Collection createObject() {
        return new Collection(collection(), data());
    }

    protected BuiltInConfiguration builtInProcess() {
        return new BuiltInConfiguration();
    }

    protected ServerConfiguration serverProcess() {
        return new ServerConfiguration();
    }

    protected void clean() {
        collection = null;
        clazz = null;
        data = null;
    }
}
