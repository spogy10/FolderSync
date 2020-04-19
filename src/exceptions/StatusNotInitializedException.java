package exceptions;

public class StatusNotInitializedException extends Exception {
    public StatusNotInitializedException() {
        this("Status not initialized");
    }

    public StatusNotInitializedException(String message) {
        super(message);
    }
}

