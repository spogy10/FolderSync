package exceptions;

public class MyFileManagerNotInitializedException extends Exception {
    public MyFileManagerNotInitializedException() {
        this("Error MyFileManager not initialized");
    }

    public MyFileManagerNotInitializedException(String message) {
        super(message);
    }
}
