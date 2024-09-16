package ua.hodik.gym.exception;

public class WrongCredentialException extends RuntimeException {
    public WrongCredentialException() {
    }

    public WrongCredentialException(String message) {
        super(message);
    }

    public WrongCredentialException(String message, Throwable cause) {
        super(message, cause);
    }
}
