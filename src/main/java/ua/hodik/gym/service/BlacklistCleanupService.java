package ua.hodik.gym.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.hodik.gym.repository.TokenBlackListRepository;

import java.util.Date;

@Service
@Log4j2
public class BlacklistCleanupService {
    @Value("${blacklist-cleaning-rate-ms}")
    private int blacklistCleaningRate;
    private final TokenBlackListRepository blackListRepository;

    public BlacklistCleanupService(TokenBlackListRepository blackListRepository) {
        this.blackListRepository = blackListRepository;
    }

    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredTokens() {
        log.debug("[BlacklistCleanupService] Deleting expired tokens");
        blackListRepository.deleteByExpirationBefore(new Date());
    }
}
