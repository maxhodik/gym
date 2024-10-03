package ua.hodik.gym.util.impl.validation;


import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import ua.hodik.gym.exception.MyValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyDtoValidatorTest {


    public static final String VALID_VALUE_1 = "ValidValue1";
    public static final String VALID_VALUE_2 = "ValidValue2";
    public static final String INVALID_VALUE_1 = "InvalidValue1";
    public static final String INVALID_VALUE_2 = "InvalidValue2";

    @Data
    private static class TestDto {
        private String field1;
        private String field2;
    }

    private final FieldError error1 = new FieldError("TestDto", "field1", "InvalidValue1", false, null, null, "Field1 is invalid");
    private final FieldError error2 = new FieldError("TestDto", "field2", "InvalidValue2", false, null, null, "Field2 is invalid");
    @Mock
    private Validator validator;

    @InjectMocks
    private MyDtoValidator myDtoValidator;

    private TestDto validDto;
    private TestDto invalidDto;

    @BeforeEach
    void setUp() {
        validDto = new TestDto();
        validDto.setField1(VALID_VALUE_1);
        validDto.setField2(VALID_VALUE_2);

        invalidDto = new TestDto();
        invalidDto.setField1(INVALID_VALUE_1);
        invalidDto.setField2(INVALID_VALUE_2);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(invalidDto, "TestDto");
    }

    @Test
    void validate_NoValidationErrors_Pass() {
        //given
        doNothing().when(validator).validate(eq(validDto), any(BeanPropertyBindingResult.class));
        //when
        assertDoesNotThrow(() -> myDtoValidator.validate(validDto));
        //then
        verify(validator).validate(eq(validDto), any(BeanPropertyBindingResult.class));
    }

    @Test
    void validate_InputIsNull_ThrowException() {
        //when
        MyValidationException exception = assertThrows(MyValidationException.class, () -> myDtoValidator.validate(null));
        //then
        assertEquals("Value can't be null", exception.getMessage());
    }

    @Test
    void validate_ValidationErrors_ThrowValidationException() {
        //given
        doAnswer(invocation -> {
            BeanPropertyBindingResult result = invocation.getArgument(1);
            result.addError(error1);
            result.addError(error2);
            return null;
        }).when(validator).validate(any(), any(BeanPropertyBindingResult.class));
        //when
        assertThrows(MyValidationException.class, () -> myDtoValidator.validate(invalidDto));
        //then
        verify(validator).validate(any(), any(BeanPropertyBindingResult.class));
    }

}
