package ua.hodik.gym.controller;

import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ua.hodik.gym.dto.ValidationErrorResponse;
import ua.hodik.gym.exception.InvalidCredentialException;
import ua.hodik.gym.exception.MyEntityNotFoundException;
import ua.hodik.gym.service.ValidationService;

import java.util.List;

@ControllerAdvice
@Log4j2
public class ExceptionHandlingController {

    public static final String TRANSACTION_ID = "transactionId";
    private final ValidationService validationService;

    @Autowired
    public ExceptionHandlingController(ValidationService validationService) {
        this.validationService = validationService;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrorResponse> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        ValidationErrorResponse validationErrorResponse = validationService.mapErrors(fieldErrors);
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        log.error("[VALIDATION] Fields contain validation errors {}, Response status: {}, TransactionId: {}",
                validationErrorResponse.getErrors(), badRequest, MDC.get(TRANSACTION_ID));
        return ResponseEntity
                .status(badRequest)
                .body(validationErrorResponse);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> onInvalidCredentialException(InvalidCredentialException e) {
        HttpStatus request = HttpStatus.UNAUTHORIZED;
        log.error("[Authentication] invalid credentials, Response status: {}, TransactionId: {}",
                request, MDC.get(TRANSACTION_ID));
        return ResponseEntity
                .status(request)
                .body(e.getMessage());
    }

    @ExceptionHandler(MyEntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ResponseEntity<String> exceptionHandler(Exception e) {
        HttpStatus request = HttpStatus.NOT_FOUND;
        log.error("{}, {}, Response status: {}, TransactionId: {}",
                e.getMessage(), e,
                request, MDC.get(TRANSACTION_ID));
        return new ResponseEntity<>(e.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<String> onHttpMessageNotReadableException(Exception e) {
        HttpStatus request = HttpStatus.BAD_REQUEST;
        log.error("{}, {}, Response status: {}, TransactionId: {}",
                e.getMessage(), e,
                request, MDC.get(TRANSACTION_ID));
        return new ResponseEntity<>(e.getMessage(), request);
    }

}