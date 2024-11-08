package ua.hodik.gym.service;

import org.springframework.security.core.Authentication;
import ua.hodik.gym.model.User;

public interface AuthService {
    Authentication getAuthentication(String token);

    User getUserFromAuth();

    User getUserFromAuth(Authentication authentication);

    String getUserName();

}
