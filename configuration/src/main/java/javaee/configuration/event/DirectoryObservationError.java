package javaee.configuration.event;

public class DirectoryObservationError {

    private String folder;
    private Exception exception;

    public DirectoryObservationError(String folder, Exception exception) {
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
