package ua.hodik.gym.jwt.impl;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ua.hodik.gym.jwt.AuthService;
import ua.hodik.gym.jwt.JwtService;
import ua.hodik.gym.model.User;
import ua.hodik.gym.repository.UserRepository;
import ua.hodik.gym.security.UserDetailsImpl;

@Service
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(JwtService jwtService,
                           UserDetailsService userDetailsService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    public User getUserFromAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUser(authentication);
    }

    @Override
    public User getUserFromAuth(Authentication authentication) {
        return getUser(authentication);
    }

    @Override
    public String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return getUserDetails(authentication).getUsername();
    }

    private User getUser(Authentication authentication) {
        if (authentication == null) {
            logger.info("[AUTH] Authentication is null");
            throw new AuthenticationServiceException("Authentication is null");
        }

        UserDetailsImpl userDetails = getUserDetails(authentication);
        String username = userDetails.getUsername();
        if (!authentication.isAuthenticated()) {
            logger.info("[AUTH] User {} is not authenticated", username);
            throw new AuthenticationServiceException("User is not authenticated");
        }
        User user = userDetails.getUser();
        if (user == null) {
            logger.info("[AUTH] Incorrect security user {}", userDetails.getUsername());
        }
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User %s snot found", username)));
    }

    private UserDetailsImpl getUserDetails(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        if (userDetails == null) {
            logger.info("[AUTH] User principals {} is not found", authentication.getName());
            throw new AuthenticationServiceException("Principal is not found");
        }
        return userDetails;
    }

    @Override
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtService.getUserName(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
