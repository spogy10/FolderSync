package exceptions;

public class SaveStatusException extends Exception {


    public SaveStatusException() {
        this("Error saving status");
    }

    public SaveStatusException(String message) {
        super(message);
    }
}
