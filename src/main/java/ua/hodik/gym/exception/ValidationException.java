package ua.hodik.gym.exception;

import java.util.Map;

public class ValidationException extends RuntimeException {
    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String validation_ran_in_service, Map<String, Map<String, String>> validationResult) {

    }
}
