package javaee.configuration.event;

public class InvalidIntegerValue {

    private String configurationValue;
    private Exception exception;

    public InvalidIntegerValue(String configurationValue, Exception exception) {
        super();
        this.configurationValue = configurationValue;
        this.exception = exception;
    }

    public String getConfigurationValue() {
        return configurationValue;
    }

    public Exception getException() {
        return exception;
    }

}
