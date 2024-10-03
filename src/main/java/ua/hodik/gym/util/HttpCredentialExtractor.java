package ua.hodik.gym.util;

import jakarta.servlet.http.HttpServletRequest;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.exception.InvalidCredentialException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HttpCredentialExtractor {
    public UserCredentialDto getCredential(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring(6);
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            String[] values = credentials.split(":", 2);
            return new UserCredentialDto(values[0], values[1]);

        } else throw new InvalidCredentialException("Incorrect credentials");
    }
}
