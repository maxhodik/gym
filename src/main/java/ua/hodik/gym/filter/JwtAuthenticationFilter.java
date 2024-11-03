package ua.hodik.gym.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ua.hodik.gym.jwt.AuthService;
import ua.hodik.gym.jwt.JwtAuthenticationException;
import ua.hodik.gym.jwt.JwtService;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final AuthService authService;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver,
            AuthService authService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.authService = authService;
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

        try {
            tryAuthenticateByToken(token);
        } catch (ExpiredJwtException ex) {

            String requestURL = request.getRequestURL().toString();
            if (requestURL.contains("refreshToken")) {
                String refreshToken = jwtService.resolveToken(request);
                try {
                    if (token == null && !jwtService.validateToken(refreshToken)) {
                        throwJwtAuthExceptionWithMessage("JWT refresh_token is expired or invalid", response);
                    }
                    Authentication usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(null, null, null);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } catch (JwtException e) {
                    throwJwtAuthExceptionWithMessage("JWT refresh_token is expired or invalid", response);
                }
            } else {
                throwJwtAuthExceptionWithMessage("JWT token is expired or invalid and it's not a refresh request", response);
            }
        } catch (JwtException e) {
            throwJwtAuthExceptionWithMessage("JWT token is expired or invalid", response);
        }
        filterChain.doFilter(request, response);
    }

    private void tryAuthenticateByToken(String token) {
        if (token != null && jwtService.validateToken(token)) {
            Authentication authentication = authService.getAuthentication(token);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
    }

    private void throwJwtAuthExceptionWithMessage(String s, ServletResponse servletResponse) throws IOException {
        SecurityContextHolder.clearContext();
        ((HttpServletResponse) servletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        throw new JwtAuthenticationException(s);
    }
}
