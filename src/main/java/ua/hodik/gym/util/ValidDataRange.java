package ua.hodik.gym.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ua.hodik.gym.util.impl.validation.CustomDateValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Target({TYPE, FIELD, PARAMETER, RECORD_COMPONENT})
@Constraint(validatedBy = CustomDateValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDataRange {
    String message() default "dateFrom must be earlier than dateTo";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
