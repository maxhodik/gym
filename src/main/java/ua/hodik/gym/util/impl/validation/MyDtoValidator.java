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
public class MyDtoValidator implements MyValidator {
    private final Validator validator;

    @Autowired
    public MyDtoValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    public <T> void validate(T value) {
        if (value == null) {
            throw new ValidationException("Value can't be null");
        }
        Map<String, List<Map<String, String>>> validationResult = getMapOfErrors(value);
        if (!validationResult.isEmpty()) {
            throw new ValidationException("Validation ran in service" + validationResult);
        }
        log.info("{}} is valid", value.getClass());
    }

    private Map<String, List<Map<String, String>>> getMapOfErrors(Object value) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(value, value.getClass().getName());
        validator.validate(value, bindingResult);
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
