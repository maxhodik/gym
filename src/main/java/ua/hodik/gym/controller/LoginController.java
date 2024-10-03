package ua.hodik.gym.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hodik.gym.dto.PasswordDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.model.User;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.util.CredentialChecker;

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
        User user = userService.findByUserName(username);
        if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
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
        userService.changePassword(id, newPassword);
        return ResponseEntity.ok("Password successfully changed");

    }
}
