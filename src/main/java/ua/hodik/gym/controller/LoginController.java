package ua.hodik.gym.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.UserDto;
import ua.hodik.gym.service.UserService;

@Log4j2
@RestController
@RequestMapping("/auth")
public class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/login")
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
}
