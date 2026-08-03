package com.interviewiq.auth.security;

import com.interviewiq.config.InterviewIqProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private InterviewIqProperties properties;
    
    @Mock
    private InterviewIqProperties.Jwt jwtProperties;

    @BeforeEach
    void setUp() {
        when(properties.getJwt()).thenReturn(jwtProperties);
        // Valid 256-bit base64 encoded key
        when(jwtProperties.getSecret()).thenReturn("q3t6w9z$C&F)J@NcQfTjWnZr4u7x!A%D*G-KaPdSgUkXp2s5v8y/B?E(H+MbQeTh");
        when(jwtProperties.getAccessTokenExpirationMs()).thenReturn(3600000L); // 1 hour
        when(jwtProperties.getRefreshTokenExpirationMs()).thenReturn(604800000L); // 7 days
        
        jwtTokenProvider = new JwtTokenProvider(properties);
    }

    @Test
    void generateAccessToken_ShouldReturnValidToken() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = new UserPrincipal(userId, "test@example.com", "password", Collections.emptyList(), true);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        // Act
        String token = jwtTokenProvider.generateAccessToken(authentication);

        // Assert
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void generateRefreshToken_ShouldReturnValidToken() {
        // Arrange
        UUID userId = UUID.randomUUID();

        // Act
        String token = jwtTokenProvider.generateRefreshToken(userId);

        // Assert
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.string"));
    }
}
