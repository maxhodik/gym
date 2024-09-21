package ua.hodik.gym.util.impl.validation;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import ua.hodik.gym.exception.ValidationException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Log4j2
public class UserCredentialDtoValidator implements MyValidator {
    private final Validator validator;

    @Autowired
    public UserCredentialDtoValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public <T> void validate(T credential) {
        Map<String, List<Map<String, String>>> validationResult = getMapOfErrors(credential);
        if (!validationResult.isEmpty()) {
            throw new ValidationException("Validation ran in service" + validationResult);
        }
        log.info("{}} is valid", credential.getClass());
    }

    private Map<String, List<Map<String, String>>> getMapOfErrors(Object credential) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(credential, credential.getClass().getName());
        validator.validate(credential, bindingResult);
        return appendErrorsToMap(bindingResult);
    }

    private Map<String, List<Map<String, String>>> appendErrorsToMap(BeanPropertyBindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                error -> Map.of(
                                        "Value", error.getRejectedValue() != null ? error.getRejectedValue().toString() : "null",
                                        "message", error.getDefaultMessage() != null ? error.getDefaultMessage() : "No message"
                                ),
                                Collectors.toList()
                        )
                ));
    }


}
