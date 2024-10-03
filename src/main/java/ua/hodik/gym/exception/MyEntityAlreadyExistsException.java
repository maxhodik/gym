package ua.hodik.gym.exception;

public class MyEntityAlreadyExistsException extends RuntimeException {
    public MyEntityAlreadyExistsException() {
    }

    public MyEntityAlreadyExistsException(String message) {
        super(message);
    }

    public MyEntityAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyEntityAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    public MyEntityAlreadyExistsException(String s, String userNameDto) {

    }
}
