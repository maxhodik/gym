package ua.hodik.gym.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceImplTest {

    private static final String USERNAME = "testUser";

    @InjectMocks
    private LoginAttemptServiceImpl loginAttemptService;

    private int maxAttempts = 3;

    private int blockDuration = 5;


    @BeforeEach
    void setUp() {

        loginAttemptService = new LoginAttemptServiceImpl();
        loginAttemptService.setMaxAttempts(maxAttempts);
        loginAttemptService.setBlockDuration(blockDuration);
    }

    @AfterEach
    void tearDown() {
        loginAttemptService.getAttemptsCache().remove(USERNAME);
        loginAttemptService.getBlockCache().remove(USERNAME);
    }

    @Test
    void testLoginFailed_BlocksAfterMaxAttempts() {
        //given
        for (int i = 0; i < maxAttempts; i++) {
            loginAttemptService.loginFailed(USERNAME);
        }
        // then
        assertTrue(loginAttemptService.isBlocked(USERNAME));
    }

    @Test
    void testLoginSucceeded_ResetsAttemptsAndUnblocks() {
        // given
        for (int i = 0; i < maxAttempts; i++) {
            loginAttemptService.loginFailed(USERNAME);
        }
        assertTrue(loginAttemptService.isBlocked(USERNAME));
        // when
        loginAttemptService.loginSucceeded(USERNAME);
        // then
        assertFalse(loginAttemptService.isBlocked(USERNAME));
        assertFalse(loginAttemptService.getAttemptsCache().containsKey(USERNAME));
        assertFalse(loginAttemptService.getBlockCache().containsKey(USERNAME));
    }

    @Test
    void testIsBlocked_UnblocksAfterDuration() {
        // given
        for (int i = 0; i < maxAttempts; i++) {
            loginAttemptService.loginFailed(USERNAME);
        }
        // when
        loginAttemptService.getBlockCache().put(USERNAME, LocalDateTime.now().minusMinutes(blockDuration + 1));
        // then
        assertFalse(loginAttemptService.isBlocked(USERNAME));
    }

    @Test
    void testLoginFailed_IncrementsAttempts() {
        // when
        loginAttemptService.loginFailed(USERNAME);
        //then
        assertEquals(1, loginAttemptService.getAttemptsCache().get(USERNAME));
        // when
        loginAttemptService.loginFailed(USERNAME);
        // then
        assertEquals(2, loginAttemptService.getAttemptsCache().get(USERNAME));
    }
}
