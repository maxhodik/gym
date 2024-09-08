package ua.hodik.gym.util.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.config.PasswordGeneratorConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordGeneratorImplTest {
    private final int PASSWORD_LENGTH = 10;
    @Mock
    private PasswordGeneratorConfig passwordGeneratorConfig;

    @InjectMocks
    private PasswordGeneratorImpl passwordGenerator;


    @Test
    void generatePassword() {
        //given
        when(passwordGeneratorConfig.getPasswordLength()).thenReturn(PASSWORD_LENGTH);
        //when
        String password = passwordGenerator.generatePassword();
        //then
        assertEquals(PASSWORD_LENGTH, password.length());
        assertTrue(password.chars().allMatch(Character::isLetter));
    }
}