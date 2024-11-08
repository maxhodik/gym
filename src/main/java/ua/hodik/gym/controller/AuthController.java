package ua.hodik.gym.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ua.hodik.gym.dto.PasswordDto;
import ua.hodik.gym.dto.UserCredentialDto;
import ua.hodik.gym.dto.ValidationErrorResponse;
import ua.hodik.gym.jwt.JwtService;
import ua.hodik.gym.model.User;
import ua.hodik.gym.service.AuthService;
import ua.hodik.gym.service.LoginAttemptService;
import ua.hodik.gym.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/auth")
public class AuthController {
    public static final String TRANSACTION_ID = "transactionId";
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthService authService;
    private final LoginAttemptService loginAttemptService;

    public AuthController(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService, AuthService authService, LoginAttemptService loginAttemptService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authService = authService;
        this.loginAttemptService = loginAttemptService;
    }

    @Operation(summary = "Login user by its credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User login",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema)),
            @ApiResponse(responseCode = "400", description = "Invalid parameter",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Too many login attempts",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content)})
    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserCredentialDto credentials) {
        String username = credentials.getUserName();
        if (loginAttemptService.isBlocked(username)) {
            log.debug("[AuthController] User {} is temporarily blocked", username);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is temporarily blocked. Try again later.");
        }
        try {
            userService.authenticate(credentials);
            String accessToken = jwtService.createToken(username, false);
            String refreshToken = jwtService.createToken(username, true);

            Map<Object, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("access_token", accessToken);
            response.put("refresh_token", refreshToken);
            loginAttemptService.loginSucceeded(username);

            log.info("User {} login successful", username);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            loginAttemptService.loginFailed(username);
            log.error("Invalid credentials. User {} unauthorized. TransactionId {}", username, MDC.get(TRANSACTION_ID));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @Operation(summary = "Change password for the authenticated  user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The password changed",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid parameter",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "User unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @PutMapping
    public ResponseEntity<String> changePassword(@RequestBody @Valid PasswordDto newPassword) {

        int id = authService.getUserFromAuth().getId();

        userService.changePassword(id, newPassword);
        log.debug("[AuthController] Changing password. TransactionId {}", MDC.get(TRANSACTION_ID));
        return ResponseEntity.ok("Password changed successfully");
    }

    @Operation(summary = "Refresh JWT token for the authenticated  user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns both new access and refresh tokens",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid parameter",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "User unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtService.resolveToken(request);
        String userName = jwtService.getUserName(refreshToken);
        User user = userService.findByUserName(userName);

        String accessToken = jwtService.createToken(user.getUserName(), false);
        Map<Object, Object> response = new HashMap<>();
        response.put("username", user.getUserName());
        response.put("access_token", accessToken);
        response.put("refresh_token", refreshToken);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "Logout user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logout",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid parameter",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "User unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content)})
    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = jwtService.resolveToken(request);
        jwtService.saveToBlacklist(token);
        log.debug("[Logout] token for user {} saved in blackList", jwtService.getUserName(token));
        return ResponseEntity.ok().body("User logout");
    }
}

