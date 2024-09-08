package ua.hodik.gym.exception;

public class StorageInitializeException extends RuntimeException {


    public StorageInitializeException() {
        super();
    }

    public StorageInitializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageInitializeException(Throwable cause) {
        super(cause);
    }

}
