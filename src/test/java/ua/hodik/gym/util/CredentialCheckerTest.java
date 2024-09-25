package ua.hodik.gym.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.exception.ValidationException;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.tets.util.TestUtils;
import ua.hodik.gym.util.impl.validation.MyValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CredentialCheckerTest {
    private static final String USER_NAME = "Sam.Jonson";
    private final String userCredentialDtoPath = "user.credential.dto.json";
    private final String userPath = "user.json";
    private final User expectedUser = TestUtils.readFromFile(userPath, User.class);
    private final UserCredentialDto userCredentialDto = TestUtils.readFromFile(userCredentialDtoPath, UserCredentialDto.class);

    @Mock
    private UserRepository userRepository;
    @Mock
    private MyValidator validator;
    @InjectMocks
    private CredentialChecker credentialChecker;

    @Test
    void matchCredential() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(userRepository.findByUserName(USER_NAME)).thenReturn(Optional.of(expectedUser));
        //when
        boolean b = credentialChecker.matchCredential(userCredentialDto);
        //then
        verify(userRepository).findByUserName(USER_NAME);
        assertTrue(b);
    }

    @Test
    void notMatchCredential() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(userRepository.findByUserName(USER_NAME)).thenReturn(Optional.empty());
        //when
        boolean b = credentialChecker.matchCredential(userCredentialDto);
        //then
        verify(userRepository).findByUserName(USER_NAME);
        assertFalse(b);
    }

    @Test
    void matchCredentialNull() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class, () -> credentialChecker.matchCredential(null));
        //then
        assertEquals("Credential can't be null", exception.getMessage());
    }

    @Test
    void matchCredentialNoValidCredential() {
        //given
        doThrow(new ValidationException()).when(validator).validate(any(UserCredentialDto.class));
        //when
        assertThrows(ValidationException.class, () -> credentialChecker.matchCredential(userCredentialDto));
    }

}