package ua.hodik.gym.util.UtilImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordGeneratorImplTest {
    private final int PASSWORD_LENGTH = 10;

    @InjectMocks
    private PasswordGeneratorImpl passwordGenerator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordGenerator.passwordLength = PASSWORD_LENGTH;
    }

    @Test
    void generatePassword() {
        String password = passwordGenerator.generatePassword();
        assertEquals(PASSWORD_LENGTH, password.length());
        assertTrue(password.chars().allMatch(Character::isLetter));
    }
}