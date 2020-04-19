package exceptions;

public class SetupStatusException extends Exception {
    public SetupStatusException() {
        this("Error setting up status");
    }

    public SetupStatusException(String message) {
        super(message);
    }
}
