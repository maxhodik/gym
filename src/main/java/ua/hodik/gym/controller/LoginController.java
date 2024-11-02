package ua.hodik.gym.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ua.hodik.gym.dto.PasswordDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.ValidationErrorResponse;
import ua.hodik.gym.model.User;
import ua.hodik.gym.service.UserService;
import ua.hodik.gym.util.CredentialChecker;

@Log4j2
@RestController
@RequestMapping("/auth")
public class LoginController {
    public static final String TRANSACTION_ID = "transactionId";
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public LoginController(UserService userService, CredentialChecker credentialChecker, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Login user by its credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User login",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid parameter",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content)})
    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid UserCredentialDto credentials) {
        String username = credentials.getUserName();
        String password = passwordEncoder.encode(credentials.getPassword());
        User user = userService.findByUserName(username);
        if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
            log.info("User {} login successful", username);
            return ResponseEntity.ok("Login successful");
        } else {
            log.error("Invalid credentials. User {} unauthorized. TransactionId {}", username, MDC.get(TRANSACTION_ID));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @Operation(summary = "Change password by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The password changed",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid parameter",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @PutMapping("/{id:\\d+}")
    public ResponseEntity<String> changeLogin(@PathVariable int id,
                                              @RequestBody @Valid PasswordDto newPassword) {
        //todo make it with properly user
        userService.changePassword(id, newPassword);
        log.debug("[LoginController] Changing password. TransactionId {}", MDC.get(TRANSACTION_ID));
        return ResponseEntity.ok("Password changed successfully");
    }
}
