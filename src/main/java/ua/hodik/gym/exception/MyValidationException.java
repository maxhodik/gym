package ua.hodik.gym.exception;

import java.util.Map;

public class MyValidationException extends RuntimeException {
    public MyValidationException() {
    }

    public MyValidationException(String message) {
        super(message);
    }

    public MyValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyValidationException(Throwable cause) {
        super(cause);
    }

    public MyValidationException(String validation_ran_in_service, Map<String, Map<String, String>> validationResult) {

    }
}
