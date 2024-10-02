package ua.hodik.gym.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hodik.gym.dto.PasswordDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.exception.InvalidCredentialException;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.util.CredentialChecker;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Log4j2
@RestController
@RequestMapping("/auth")
public class LoginController {
    private final UserService userService;
    private final CredentialChecker credentialChecker;

    public LoginController(UserService userService, CredentialChecker credentialChecker) {
        this.userService = userService;
        this.credentialChecker = credentialChecker;
    }


    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UserCredentialDto credentials) {
        String username = credentials.getUserName();
        String password = credentials.getPassword();
        UserDto userFromDB = userService.findByUserName(username);
        if (userFromDB.getUserName().equals(username) && userFromDB.getPassword().equals(password)) {
            log.info("User {} login successful", username);
            return ResponseEntity.ok("Login successful");
        } else {
            log.error("Invalid credentials. User {} unauthorized", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<String> changeLogin(@PathVariable int id,
                                              @RequestBody @Valid PasswordDto newPassword,
                                              HttpServletRequest request) {
        credentialChecker.checkIfMatchCredentialsOrThrow(getCredential(request));
        userService.changePassword(id, newPassword);
        return ResponseEntity.ok("Password successfully changed");

    }

    protected UserCredentialDto getCredential(HttpServletRequest request) {

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
