package exceptions;

public class StatusNotIntializedException extends Exception {


    public StatusNotIntializedException() {
        this("Status not initialized");
    }

    public StatusNotIntializedException(String message) {
        super(message);
    }
}
