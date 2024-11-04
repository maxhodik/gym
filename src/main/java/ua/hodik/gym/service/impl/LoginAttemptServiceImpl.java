package ua.hodik.gym.service.impl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.hodik.gym.service.LoginAttemptService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {
    @Value("${max_failed_attempts}")
    private int maxAttempts;
    @Value("${block_duration_minutes}")
    private int blockDuration;

    private final Map<String, Integer> attemptsCache = new HashMap<>();
    private final Map<String, LocalDateTime> blockCache = new HashMap<>();


    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
        blockCache.remove(username);
    }


    public void loginFailed(String username) {
        int attempts = attemptsCache.getOrDefault(username, 0);
        attempts++;
        attemptsCache.put(username, attempts);

        if (attempts >= maxAttempts) {
            blockCache.put(username, LocalDateTime.now().plusMinutes(blockDuration));
        }
    }

    public boolean isBlocked(String username) {
        if (!blockCache.containsKey(username)) {
            return false;
        }
        LocalDateTime blockedUntil = blockCache.get(username);
        if (LocalDateTime.now().isAfter(blockedUntil)) {
            blockCache.remove(username);
            attemptsCache.remove(username);
            return false;
        }
        return true;
    }
}
