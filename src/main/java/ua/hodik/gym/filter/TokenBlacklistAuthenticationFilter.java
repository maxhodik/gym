package ua.hodik.gym.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.hodik.gym.jwt.JwtAuthenticationException;
import ua.hodik.gym.jwt.JwtService;
import ua.hodik.gym.repository.TokenBlackListRepository;

import java.io.IOException;

@Component
@Log4j2
public class TokenBlacklistAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final TokenBlackListRepository tokenRepository;

    public TokenBlacklistAuthenticationFilter(
            JwtService jwtService, TokenBlackListRepository tokenRepository) {
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String token = jwtService.resolveToken(request);
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (tokenRepository.existsById(token)) {
            log.debug("[AuthFilter] JWT token in the Blacklist");
            throwJwtAuthExceptionWithMessage("JWT token is expired or invalid", response);
        }
        filterChain.doFilter(request, response);
    }


    private void throwJwtAuthExceptionWithMessage(String s, HttpServletResponse servletResponse) throws IOException {
        SecurityContextHolder.clearContext();
        servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        throw new JwtAuthenticationException(s);
    }
}
