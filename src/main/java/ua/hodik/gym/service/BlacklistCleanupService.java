package ua.hodik.gym.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.hodik.gym.repository.TokenBlackListRepository;

import java.util.Date;

@Service
@Log4j2
public class BlacklistCleanupService {

    private final TokenBlackListRepository blackListRepository;

    public BlacklistCleanupService(TokenBlackListRepository blackListRepository) {
        this.blackListRepository = blackListRepository;
    }

    @Scheduled(fixedRateString = "${blacklist-cleaning-rate-ms}")
    public void cleanupExpiredTokens() {
        log.debug("[BlacklistCleanupService] Deleting expired tokens");
        blackListRepository.deleteByExpirationBefore(new Date());
    }
}
