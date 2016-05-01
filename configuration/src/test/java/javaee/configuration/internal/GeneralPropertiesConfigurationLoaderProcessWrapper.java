package javaee.configuration.internal;

import java.io.IOException;
import java.io.InputStream;

import javaee.configuration.internal.PropertiesLoaderProcess;

public class GeneralPropertiesConfigurationLoaderProcessWrapper extends PropertiesLoaderProcess {

    @Override
    public void prepare() {

    }

    @Override
    public InputStream propertiesInputStream() {
        return null;
    }

    @Override
    public void eventErrorOnPropertiesLoad(IOException exception) {

    }

}
