package ua.hodik.gym.util.impl.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.hodik.gym.dto.FilterFormDto;
import ua.hodik.gym.util.ValidDataRange;

import java.time.LocalDate;

public class CustomDateValidator implements ConstraintValidator<ValidDataRange, FilterFormDto> {


    @Override
    public boolean isValid(FilterFormDto value, ConstraintValidatorContext context) {
        LocalDate dateFrom = value.getDateFrom();
        LocalDate dateTo = value.getDateTo();

        if (dateFrom == null || dateTo == null) {
            return true;
        }
        if (dateFrom.isAfter(dateTo)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("dateFrom must be earlier than or equal to dateTo")
                    .addPropertyNode("dateFrom")
                    .addConstraintViolation();
            return false;
        }
        return true;
    }
}


