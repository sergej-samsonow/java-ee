package javaee.configuration.internal;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import javaee.configuration.Collection;
import javaee.configuration.Configuration;

@Stateless
public class ConfigurationProducer {

    @Produces
    @Configuration
    public Collection collection(InjectionPoint ip) {
        String collection = ip.getAnnotated().getAnnotation(Configuration.class).value();
        if (collection == null || collection.isEmpty()) {
            return null; // empty collection
        }
        Bean<?> bean = ip.getBean();
        return collection(bean, collection);
    }

    private Collection collection(Bean<?> bean, String name) {
        return null;
    }
}
