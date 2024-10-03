package ua.hodik.gym.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.exception.InvalidCredentialException;
import ua.hodik.gym.exception.MyValidationException;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.tets.util.TestUtils;
import ua.hodik.gym.util.impl.validation.MyValidator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void checkIfMatchCredentialsOrThrow_Valid() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(userRepository.findByUserName(USER_NAME)).thenReturn(Optional.of(expectedUser));
        //when
        credentialChecker.checkIfMatchCredentialsOrThrow(userCredentialDto);
        //then
        verify(validator).validate(userCredentialDto);
        verify(userRepository).findByUserName(USER_NAME);
    }

    @Test
    void notMatchCredential_ValidNotFoundInDB_ThrowException() {
        //given
        doNothing().when(validator).validate(any(UserCredentialDto.class));
        when(userRepository.findByUserName(USER_NAME)).thenReturn(Optional.empty());
        //when
        InvalidCredentialException exception = assertThrows(InvalidCredentialException.class,
                () -> credentialChecker.checkIfMatchCredentialsOrThrow(userCredentialDto));
        //then
        verify(validator).validate(userCredentialDto);
        verify(userRepository).findByUserName(USER_NAME);
        assertEquals("Incorrect credentials, this operation is prohibited", exception.getMessage());

    }

    @Test
    void matchCredential_Null_ThrowException() {
        //when
        NullPointerException exception = assertThrows(NullPointerException.class, () -> credentialChecker.matchCredential(null));
        //then
        assertEquals("Credential can't be null", exception.getMessage());
    }

    @Test
    void matchCredential_NoValidCredential_ThrowException() {
        //given
        doThrow(new MyValidationException()).when(validator).validate(any(UserCredentialDto.class));
        //when
        assertThrows(MyValidationException.class, () -> credentialChecker.matchCredential(userCredentialDto));
        //then
        verify(userRepository, never()).findByUserName(USER_NAME);
    }

}