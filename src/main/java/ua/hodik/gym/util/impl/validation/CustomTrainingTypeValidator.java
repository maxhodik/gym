package ua.hodik.gym.util.impl.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CustomTrainingTypeValidator implements ConstraintValidator<ValidTrainingTypeEnum, String> {
    private List<String> valueList;

    //for test only
    @Override
    public void initialize(ValidTrainingTypeEnum constraintAnnotation) {
        valueList = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return valueList.contains(value.toUpperCase());
    }

    protected void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }

}
