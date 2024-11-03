package ua.hodik.gym.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private final JwtConfig jwtConfig;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String createToken(String username, boolean isRefreshToken) {
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());

        Claims build = Jwts.claims()
                .subject(username)
                .build();
        LocalDateTime now = LocalDateTime.now();
        Date validity;
        if (isRefreshToken) {
            LocalDateTime resultDate = now.plusDays(jwtConfig.getRefreshValidityInDays());
            validity = java.sql.Timestamp.valueOf(resultDate);
        } else {
            LocalDateTime resultDate = now.plusHours(jwtConfig.getValidityInHours());
            validity = java.sql.Timestamp.valueOf(resultDate);
        }
        return Jwts.builder()
                .claims(build)
                .issuedAt(java.sql.Timestamp.valueOf(now))
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean validateToken(String token) {
        Claims claims = extractAllClaims(token);
        return !claims.getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
    }

    public String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader(jwtConfig.getAuthorizationHeader());
        if (authHeader != null && authHeader.contains(jwtConfig.getTokenPrefix())) {
            return authHeader.substring(7);
        }
        //todo add log Inconsist authHeader
        return null;
    }


    public String getUserName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();

    }


}

