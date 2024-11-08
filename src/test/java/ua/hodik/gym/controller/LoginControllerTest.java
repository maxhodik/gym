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
import ua.hodik.gym.jwt.JwtService;
import ua.hodik.gym.model.User;
import ua.hodik.gym.service.AuthService;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.service.impl.LoginAttemptServiceImpl;
import ua.hodik.gym.tets.util.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {
    public static final int ID = 1;
    private static final String USER_NAME = "Sam.Jonson";
    private static final PasswordDto NEW_PASSWORD_DTO = new PasswordDto("1111111");
    public static final String TOKEN = "token";
    private final String userPath = "user.json";
    private final String invalidCredentialPath = "invalid.user.credential.dto.json";

    private final String userCredentialDtoPath = "user.credential.dto.json";
    private final String responseBody = "expected.response.body.json";
    private final User user = TestUtils.readFromFile(userPath, User.class);
    private final UserCredentialDto userCredentialDto = TestUtils.readFromFile(userCredentialDtoPath, UserCredentialDto.class);
    private final UserCredentialDto invalidUserCredentialDto = TestUtils.readFromFile(invalidCredentialPath, UserCredentialDto.class);
    private final Object expectedResponseBody = TestUtils.readFromFile(responseBody, Object.class);
    @Mock
    private UserService userService;
    @Mock
    private LoginAttemptServiceImpl loginAttemptService;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;

    @Test
    void login_ValidCredentials_ResponseOk() {
        //given
        when(userService.authenticate(any(UserCredentialDto.class))).thenReturn(user);
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        when(jwtService.createToken(anyString(), any(Boolean.class))).thenReturn(TOKEN);
        //when
        ResponseEntity<?> response = authController.login(userCredentialDto);
        //then
        verify(userService).authenticate(userCredentialDto);
        verify(loginAttemptService).loginSucceeded(userCredentialDto.getUserName());
        assertEquals(expectedResponseBody, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void login_InvalidCredentials_ResponseUnauthorized() {
        //given
        when(userService.authenticate(any(UserCredentialDto.class))).thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        //when
        ResponseEntity<?> response = authController.login(invalidUserCredentialDto);
        //then
        verify(userService).authenticate(invalidUserCredentialDto);
        verify(loginAttemptService).loginFailed(invalidUserCredentialDto.getUserName());
        assertEquals("Invalid credentials", response.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void changeLogin_ValidPassword_ResponseOK() {
        //given
        when(authService.getUserFromAuth()).thenReturn(user);
        //when
        ResponseEntity<String> response = authController.changePassword(NEW_PASSWORD_DTO);
        //then
        verify(userService).changePassword(0, NEW_PASSWORD_DTO);
        assertEquals("Password changed successfully", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
