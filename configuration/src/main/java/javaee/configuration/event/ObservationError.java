package javaee.configuration.event;

public class ObservationError {

    private String folder;
    private Exception exception;

    public ObservationError(String folder, Exception exception) {
        super();
        this.folder = folder;
    }

    public Exception getException() {
        return exception;
    }

    public String getFolder() {
        return folder;
    }

}
