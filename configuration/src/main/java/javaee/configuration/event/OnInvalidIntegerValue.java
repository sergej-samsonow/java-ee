package javaee.configuration.event;

public class OnInvalidIntegerValue {

    private String collection;
    private String key;
    private String value;
    private NumberFormatException exception;

    public OnInvalidIntegerValue(String collection, String key, String entry, NumberFormatException exception) {
        super();
        this.collection = collection;
        this.key = key;
        this.value = entry;
        this.exception = exception;
    }

    public String getCollection() {
        return collection;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public NumberFormatException getException() {
        return exception;
    }

}
