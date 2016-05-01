package javaee.configuration.internal;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.event.Event;

import javaee.configuration.event.BuiltInPropertiesError;
import javaee.configuration.event.BuiltInConfigurationNotFound;

public class BuiltInConfiguration extends PropertiesLoaderProcess {

    private Class<?> clazz;
    private InputStream stream;

    private Event<BuiltInConfigurationNotFound> notFound;
    private Event<BuiltInPropertiesError> errorOnLoad;

    @Override
    public void prepare() {
        openStream();
        enable();
        if (streamIsEmpty()) {
            notFound.fire(new BuiltInConfigurationNotFound(getCollection(), getConfigurationFor(), getPath()));
            disable();
        }
    }

    public void setNotFound(Event<BuiltInConfigurationNotFound> notFound) {
        this.notFound = notFound;
    }

    public void setErrorOnLoad(Event<BuiltInPropertiesError> errorOnLoad) {
        this.errorOnLoad = errorOnLoad;
    }

    public void setConfigurationFor(Class<?> clazz) {
        this.clazz = clazz;
    }

    protected Class<?> getConfigurationFor() {
        return clazz;
    }

    protected boolean streamIsEmpty() {
        return stream == null;
    }

    protected void openStream() {
        stream = clazz.getResourceAsStream(getPath());
    }

    protected String getPath() {
        return "/" + getCollection() + ".properties";
    }

    @Override
    public InputStream propertiesInputStream() {
        return stream;
    }

    @Override
    public void eventErrorOnPropertiesLoad(IOException exception) {
        errorOnLoad.fire(new BuiltInPropertiesError(getCollection(), getConfigurationFor(), getPath(), exception));
    }

    @Override
    public String cacheId() {
        return getConfigurationFor().getName() + "." + getCollection();
    }

}
