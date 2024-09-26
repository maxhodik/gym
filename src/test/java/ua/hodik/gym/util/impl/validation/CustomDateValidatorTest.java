package ua.hodik.gym.util.impl.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintViolationBuilder;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.dto.FilterFormDto;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomDateValidatorTest {
    private final LocalDate now = LocalDate.now();
    private FilterFormDto filterFormDto;
    private final CustomDateValidator validator = new CustomDateValidator();
    @Mock
    private ConstraintValidatorContextImpl context;
    @Mock
    private HibernateConstraintViolationBuilder violationBuilder;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder;

    @Test
    void isValid_DateFromIsBeforeDateTo_ReturnTrue() {
        //given
        filterFormDto = new FilterFormDto(now, now.plusDays(1));
        //when
        assertTrue(validator.isValid(filterFormDto, context));
        //then
        verify(context, never()).buildConstraintViolationWithTemplate("dateFrom must be earlier than or equal to dateTo");
        verify(violationBuilder, never()).addPropertyNode("dateFrom");
        verify(nodeBuilder, never()).addConstraintViolation();
    }

    @Test
    void isValid_DateFromIsEqualsDateTo_ReturnTrue() {
        //given
        filterFormDto = new FilterFormDto(now, now);
        //when
        assertTrue(validator.isValid(filterFormDto, context));
        //then
        verify(context, never()).buildConstraintViolationWithTemplate("dateFrom must be earlier than or equal to dateTo");
        verify(violationBuilder, never()).addPropertyNode("dateFrom");
        verify(nodeBuilder, never()).addConstraintViolation();
    }

    @Test
    void isValid_DateFromIsNull_ReturnTrue() {
        //given
        filterFormDto = new FilterFormDto(null, now);
        //when
        assertTrue(validator.isValid(filterFormDto, context));
        //then
        verify(context, never()).buildConstraintViolationWithTemplate("dateFrom must be earlier than or equal to dateTo");
        verify(violationBuilder, never()).addPropertyNode("dateFrom");
        verify(nodeBuilder, never()).addConstraintViolation();
    }

    @Test
    void isValid_DateToIsNull_ReturnTrue() {
        //given
        filterFormDto = new FilterFormDto(now, null);
        //when
        assertTrue(validator.isValid(filterFormDto, context));
        //then
        verify(context, never()).buildConstraintViolationWithTemplate("dateFrom must be earlier than or equal to dateTo");
        verify(violationBuilder, never()).addPropertyNode("dateFrom");
        verify(nodeBuilder, never()).addConstraintViolation();
    }

    @Test
    void isValid_DateFromIsNullDateToIsNull_ReturnTrue() {
        //given
        filterFormDto = new FilterFormDto(null, null);
        //when,then
        assertTrue(validator.isValid(filterFormDto, context));
        //then
        verify(context, never()).buildConstraintViolationWithTemplate("dateFrom must be earlier than or equal to dateTo");
        verify(violationBuilder, never()).addPropertyNode("dateFrom");
        verify(nodeBuilder, never()).addConstraintViolation();
    }

    @Test
    void isValid_DateFromIsAfterDateTo_ReturnFalse() {
        //given
        filterFormDto = new FilterFormDto(now.plusDays(1), now);
        doNothing().when(context).disableDefaultConstraintViolation();
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        //when
        assertFalse(validator.isValid(filterFormDto, context));
        //then
        verify(context).buildConstraintViolationWithTemplate("dateFrom must be earlier than or equal to dateTo");
        verify(violationBuilder).addPropertyNode("dateFrom");
        verify(nodeBuilder).addConstraintViolation();
    }
}