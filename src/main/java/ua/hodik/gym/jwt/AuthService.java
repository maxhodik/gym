package ua.hodik.gym.jwt;

import org.springframework.security.core.Authentication;
import ua.hodik.gym.model.User;

public interface AuthService {
    Authentication getAuthentication(String token);

    User getUserFromAuth();

    User getUserFromAuth(Authentication authentication);

    String getUserName();

}
