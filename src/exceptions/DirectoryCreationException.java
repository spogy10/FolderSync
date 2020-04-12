package exceptions;

public class DirectoryCreationException extends Exception{
    public DirectoryCreationException() {
        this("Unable to create directory");
    }

    public DirectoryCreationException(String message) {
        super(message);
    }
}
