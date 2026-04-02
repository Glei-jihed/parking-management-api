package edu.backend.infrastructure.out.security.security;

import edu.backend.domain.model.User;
import edu.backend.domain.port.out.TokenProviderPort;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private final Key signingKey;
    private final long expirationHours;

    public JwtTokenProviderAdapter(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-hours}") long expirationHours
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationHours = expirationHours;
    }

    @Override
    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(expirationHours * 3600);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parse(token).getPayload().getSubject();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) signingKey)
                .build()
                .parseSignedClaims(token);
    }
}