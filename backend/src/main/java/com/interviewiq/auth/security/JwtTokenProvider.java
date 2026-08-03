package com.interviewiq.auth.security;

import com.interviewiq.config.InterviewIqProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class for generating and validating JWT tokens.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final InterviewIqProperties interviewIqProperties;
    private final SecretKey key;

    public JwtTokenProvider(InterviewIqProperties interviewIqProperties) {
        this.interviewIqProperties = interviewIqProperties;
        byte[] keyBytes = Decoders.BASE64.decode(interviewIqProperties.getJwt().getSecret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return generateToken(userPrincipal.getId(), interviewIqProperties.getJwt().getAccessTokenExpirationMs());
    }

    public String generateRefreshToken(UUID userId) {
        return generateToken(userId, interviewIqProperties.getJwt().getRefreshTokenExpirationMs());
    }

    private String generateToken(UUID userId, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return UUID.fromString(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}
