package ua.hodik.gym.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.hodik.gym.repository.TokenBlackListRepository;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BlacklistCleanupServiceTest {
    @Mock
    private TokenBlackListRepository blackListRepository;
    @InjectMocks
    private BlacklistCleanupService blacklistCleanupService;

    @Test
    void testCleanupExpiredTokens() {
        // when
        blacklistCleanupService.cleanupExpiredTokens();
        // then
        verify(blackListRepository, times(1)).deleteByExpirationBefore(any(Date.class));
    }
}