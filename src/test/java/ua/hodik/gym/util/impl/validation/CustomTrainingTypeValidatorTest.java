package ua.hodik.gym.util.impl.validation;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.model.TrainingType;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CustomTrainingTypeValidatorTest {
    public static final String CORRECT_VALUE = "ZUMBA";
    public static final String INCORRECT_VALUE = "Zum";
    public static final String CORRECT_VALUE_LOWER_CASE = "zuma";
    @Mock
    private ValidTrainingTypeEnum mockAnnotation;
    private List<String> valueList;
    private final CustomTrainingTypeValidator validator = new CustomTrainingTypeValidator();
    @Mock
    private ConstraintValidatorContextImpl context;

    @BeforeEach
    void setUp() {
        valueList = Arrays.stream(TrainingType.values()).map(Enum::name).toList();
        validator.setValueList(valueList);
    }

    @Test
    void isValid_CorrectValue_ReturnTrue() {
        assertTrue(validator.isValid(CORRECT_VALUE, context));
    }

    @Test
    void isValid_CorrectValueLowerCase_ReturnTrue() {
        assertFalse(validator.isValid(CORRECT_VALUE_LOWER_CASE, context));
    }

    @Test
    void isValid_IncorrectValue_ReturnFalse() {
        assertFalse(validator.isValid(INCORRECT_VALUE, context));
    }
}