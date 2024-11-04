package ua.hodik.gym.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hodik.gym.model.TokensBlackList;
import ua.hodik.gym.repository.TokenBlackListRepository;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.Function;

@Service
@Log4j2
public class JwtService {
    private final JwtConfig jwtConfig;
    private final TokenBlackListRepository blackListRepository;

    public JwtService(JwtConfig jwtConfig, TokenBlackListRepository blackListRepository) {
        this.jwtConfig = jwtConfig;
        this.blackListRepository = blackListRepository;
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
        log.debug("[JWT Service] Authorization header does not contain the expected token prefix ");
        return null;
    }


    public String getUserName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public void saveToBlacklist(String token) {
        TokensBlackList tokensBlackLIst = TokensBlackList.builder()
                .body(token)
                .expiration(extractExpiration(token))
                .build();
        blackListRepository.save(tokensBlackLIst);
    }

    public void removeExpiredToken() {
        blackListRepository.deleteByExpirationBefore(new Date());
    }
}

