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
import ua.hodik.gym.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyDtoValidatorTest {


    @Data
    private static class TestDto {
        private String field1;
        private String field2;
    }

    private final FieldError error = new FieldError("TestDto", "field1", "InvalidValue1", false, null, null, "Field1 is invalid");
    private final FieldError error1 = new FieldError("TestDto", "field2", "InvalidValue2", false, null, null, "Field2 is invalid");
    private final FieldError error2 = new FieldError("TestDto", "field1", "InvalidValue1", false, null, null, "Field1 is invalid");
    private final FieldError error3 = new FieldError("TestDto", "field2", "InvalidValue2", false, null, null, "Field2 is invalid");
    @Mock
    private Validator validator;

    @InjectMocks
    private MyDtoValidator myDtoValidator;

    private TestDto validDto;
    private TestDto invalidDto;

    @BeforeEach
    void setUp() {
        validDto = new TestDto();
        validDto.setField1("ValidValue1");
        validDto.setField2("ValidValue2");

        invalidDto = new TestDto();
        invalidDto.setField1("InvalidValue1");
        invalidDto.setField2("InvalidValue2");
    }

    @Test
    void validate_shouldPass_whenNoValidationErrors() {
        //given
        doNothing().when(validator).validate(eq(validDto), any(BeanPropertyBindingResult.class));
        //when
        assertDoesNotThrow(() -> myDtoValidator.validate(validDto));
        //then
        verify(validator).validate(eq(validDto), any(BeanPropertyBindingResult.class));
    }

    @Test
    void validate_shouldThrowException_whenInputIsNull() {
        //when
        ValidationException exception = assertThrows(ValidationException.class, () -> myDtoValidator.validate(null));

        //then
        assertEquals("Value can't be null", exception.getMessage());
    }


    @Test
    void validate_shouldThrowValidationException_whenThereAreValidationErrors() {
        //given
        invalidDto.setField1("InvalidValue1");
        invalidDto.setField2("InvalidValue2");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(invalidDto, "TestDto");

        doAnswer(invocation -> {
            BeanPropertyBindingResult result = invocation.getArgument(1);

            result.addError(error2);
            result.addError(error3);
            return null;
        }).when(validator).validate(any(), any(BeanPropertyBindingResult.class));
        //when
        ValidationException exception = assertThrows(ValidationException.class, () -> myDtoValidator.validate(invalidDto));
        //then
        verify(validator).validate(any(), any(BeanPropertyBindingResult.class));
    }

}
