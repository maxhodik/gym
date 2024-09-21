package ua.hodik.gym.util;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ua.hodik.gym.util.impl.validation.CustomTrainingTypeValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Target({TYPE, FIELD, PARAMETER, RECORD_COMPONENT})
@Constraint(validatedBy = CustomTrainingTypeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTrainingTypeEnum {
    Class<? extends Enum<?>> enumClass();

    String message() default "Wrong training type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
