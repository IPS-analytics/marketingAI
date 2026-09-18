package vitua.kotler.ai.services.impl;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import vitua.kotler.ai.entitys.UserEntity;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl();
        ReflectionTestUtils.setField(jwtService, "jwtSigningKey",
                "53A73E5F1C4E0A2D3B5F2D784E6A1B423D6F247D1F6E5C3A596D635A75327855");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800000L);
    }

    @Test
    void generateAndValidateToken() {
        UserDetails user = User.withUsername("ivan").password("pwd").authorities(Collections.emptyList()).build();
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.validateToken(token));
        assertEquals("ivan", jwtService.extractUserName(token));
        assertTrue(jwtService.isTokenValid(token, user));
        assertFalse(jwtService.isTokenExpired(token));
        assertTrue(jwtService.extractExpiration(token).after(new Date()));
    }

    @Test
    void generateTokenIncludesUserEntityClaims() {
        UserEntity user = UserEntity.builder()
                .id(42L)
                .username("anna")
                .email("anna@example.com")
                .password("secret")
                .build();

        String token = jwtService.generateToken(user);
        assertEquals("anna", jwtService.extractUserName(token));
        assertEquals("anna@example.com", jwtService.extractAllClaims(token).get("email"));
        assertEquals(42, ((Number) jwtService.extractAllClaims(token).get("id")).intValue());
    }

    @Test
    void expiredTokenIsInvalid() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        UserDetails user = User.withUsername("ivan").password("pwd").authorities(Collections.emptyList()).build();
        String token = jwtService.generateToken(user);

        assertFalse(jwtService.validateToken(token));
        assertThrows(ExpiredJwtException.class, () -> jwtService.extractUserName(token));
    }

    @Test
    void invalidTokenIsRejected() {
        assertFalse(jwtService.validateToken("not-a-jwt"));
        assertFalse(jwtService.validateToken(""));
    }

    @Test
    void refreshTokenContainsUsername() {
        UserDetails user = User.withUsername("ivan").password("pwd").authorities(Collections.emptyList()).build();
        String refresh = jwtService.generateRefreshToken(user);
        assertTrue(jwtService.validateToken(refresh));
        assertEquals("ivan", jwtService.extractUserName(refresh));
    }
}
