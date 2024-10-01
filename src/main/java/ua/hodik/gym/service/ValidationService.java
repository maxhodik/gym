package ua.hodik.gym.service;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
import ua.hodik.gym.dto.ValidationErrorResponse;

import java.util.*;
import java.util.function.BiFunction;

@Service
public class ValidationService {


    public ValidationErrorResponse mapErrors(List<FieldError> errors) {
        ValidationErrorResponse validationErrors = new ValidationErrorResponse();
        Map<String, Set<String>> errorsMap = new HashMap<>();
        for (FieldError error : errors) {
            if (StringUtils.isAllBlank(error.getField(), error.getDefaultMessage())) {
                continue;
            }
            errorsMap.computeIfAbsent(error.getField(), k -> new HashSet<>()).add(error.getDefaultMessage());
            errorsMap.computeIfPresent(error.getField(), addMessageToMap(error));
        }
        validationErrors.setErrors(errorsMap);
        return validationErrors;
    }

    private BiFunction<String, Set<String>, Set<String>> addMessageToMap(FieldError error) {
        return (key, val) -> {
            val.add(error.getDefaultMessage());
            return val;
        };
    }
}
