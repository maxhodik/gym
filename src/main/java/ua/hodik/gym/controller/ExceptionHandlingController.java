package ua.hodik.gym.controller;

import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

    public static final String TRANSACTION_ID = "transactionId";
    private final ValidationService validationService;

    public ExceptionHandlingController(ValidationService validationService) {
        this.validationService = validationService;
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
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
    public ResponseEntity<String> onInvalidCredentialException(InvalidCredentialException e) {
        HttpStatus request = HttpStatus.UNAUTHORIZED;
        log.error("[Authentication] invalid credentials, Response status: {}, TransactionId: {}",
                request, MDC.get(TRANSACTION_ID));
        return ResponseEntity
                .status(request)
                .body(e.getMessage());
    }

    @ExceptionHandler({MyEntityNotFoundException.class, HttpMessageNotReadableException.class})
    private ResponseEntity<String> exceptionHandler(Exception e) {
        HttpStatus request = HttpStatus.BAD_REQUEST;
        log.error("{}, {}, Response status: {}, TransactionId: {}",
                e.getMessage(), e,
                request, MDC.get(TRANSACTION_ID));
        return new ResponseEntity<>(e.getMessage(), request);
    }

}