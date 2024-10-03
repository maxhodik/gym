package ua.hodik.gym.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.hodik.gym.dto.ValidationErrorResponse;
import ua.hodik.gym.exception.InvalidCredentialException;
import ua.hodik.gym.exception.MyEntityNotFoundException;
import ua.hodik.gym.service.ValidationService;

import java.util.List;

@ControllerAdvice
@Log4j2
public class ExceptionHandlingController {

    private final ValidationService validationService;

    public ExceptionHandlingController(ValidationService validationService) {
        this.validationService = validationService;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ValidationErrorResponse> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        ValidationErrorResponse validationErrorResponse = validationService.mapErrors(fieldErrors);
        log.error("[VALIDATION] Fields contain validation errors {}", validationErrorResponse.getErrors());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(validationErrorResponse);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    @ResponseBody
    public ResponseEntity<String> onInvalidCredentialException(InvalidCredentialException e) {
        log.error("[Authentication] invalid credentials");
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }

    @ExceptionHandler(MyEntityNotFoundException.class)
    private ResponseEntity<String> exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}