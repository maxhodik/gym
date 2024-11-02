package ua.hodik.gym.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ua.hodik.gym.dto.PasswordDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.model.User;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.tets.util.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {
    public static final int ID = 1;
    private static final String USER_NAME = "Sam.Jonson";
    private static final PasswordDto NEW_PASSWORD_DTO = new PasswordDto("1111111");
    private final String userPath = "user.json";
    private final String invalidCredentialPath = "invalid.user.credential.dto.json";

    private final String userCredentialDtoPath = "user.credential.dto.json";
    private final User user = TestUtils.readFromFile(userPath, User.class);
    private final UserCredentialDto userCredentialDto = TestUtils.readFromFile(userCredentialDtoPath, UserCredentialDto.class);
    private final UserCredentialDto invalidUserCredentialDto = TestUtils.readFromFile(invalidCredentialPath, UserCredentialDto.class);

    @Mock
    private UserService userService;
    @InjectMocks
    private AuthController authController;

    @Test
    void login_ValidCredentials_ResponseOk() {
        //given
        when(userService.findByUserName(anyString())).thenReturn(user);
        //when
        ResponseEntity<String> response = authController.login(userCredentialDto);
        //then
        verify(userService).findByUserName(USER_NAME);
        assertEquals("Login successful", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void login_InvalidCredentials_ResponseUnauthorized() {
        //given
        when(userService.findByUserName(anyString())).thenReturn(user);
        //when
        ResponseEntity<String> response = authController.login(invalidUserCredentialDto);
        //then
        verify(userService).findByUserName(invalidUserCredentialDto.getUserName());
        assertEquals("Invalid credentials", response.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void changeLogin_ValidPassword_ResponseOK() {
        //when
        ResponseEntity<String> response = authController.changeLogin(ID, NEW_PASSWORD_DTO);
        //then
        verify(userService).changePassword(ID, NEW_PASSWORD_DTO);
        assertEquals("Password changed successfully", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
