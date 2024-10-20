package ua.hodik.gym.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua.hodik.gym.exception.EntityNotFoundException;
import ua.hodik.gym.model.User;
import ua.hodik.gym.service.UserService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class BasicAuthFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Autowired
    public BasicAuthFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.contains("/registration") || path.contains("/auth/login") ||
                path.contains("/swagger-ui") || path.contains("/v3/api-docs")
                || path.contains("/actuator") || path.contains("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring(6);
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);

            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String password = values[1];
            User user;
            try {
                user = userService.findByUserName(username);
            } catch (EntityNotFoundException e) {
                sendUnauthorizedResponse(response);
                return;
            }
            if (user.getPassword().equals(password)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        sendUnauthorizedResponse(response);
    }


    private static void sendUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("Unauthorized access");
    }
}
