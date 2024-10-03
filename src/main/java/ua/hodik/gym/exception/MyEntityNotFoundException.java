package ua.hodik.gym.exception;

public class MyEntityNotFoundException extends RuntimeException {
    public MyEntityNotFoundException() {
    }

    public MyEntityNotFoundException(String message) {
        super(message);
    }

    public MyEntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
