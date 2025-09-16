package org.ebndrnk.authorizationservice.unit;

import io.jsonwebtoken.Claims;
import org.ebndrnk.authorizationservice.exception.token.InvalidTokenException;
import org.ebndrnk.authorizationservice.exception.token.TokenExpiredException;
import org.ebndrnk.authorizationservice.exception.user.UserNotFoundException;
import org.ebndrnk.authorizationservice.model.dto.JwtResponse;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.ebndrnk.authorizationservice.model.entity.user.UserRole;
import org.ebndrnk.authorizationservice.model.entity.token.RefreshToken;
import org.ebndrnk.authorizationservice.repository.RefreshTokenRepository;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.ebndrnk.authorizationservice.service.jwt.JwtService;
import org.ebndrnk.authorizationservice.util.DeviceIdExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private DeviceIdExtractor deviceIdExtractor;

    private final String secretKey = "mysupersecretkeymysupersecretkey"; // 256-bit key
    private final long accessTtl = 1000 * 60 * 5; // 5 minutes
    private final long refreshTtl = 1000 * 60 * 60 * 24; // 24 hours

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtService, "secret", secretKey);
        ReflectionTestUtils.setField(jwtService, "accessTokenExpirationMs", accessTtl);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpirationMs", refreshTtl);
        jwtService.init();
    }

    @Test
    void testGenerateAccessToken() {
        String token = jwtService.generateAccessToken("test@example.com", "USER");
        assertNotNull(token);
    }

    @Test
    void testGenerateRefreshToken() {
        String token = jwtService.generateRefreshToken("test@example.com", "USER");
        assertNotNull(token);
    }

    @Test
    void testValidateToken_valid() {
        String token = jwtService.generateAccessToken("test@example.com", "USER");
        assertTrue(jwtService.validateToken(token));
    }

    @Test
    void testValidateToken_invalid_shouldThrow() {
        assertThrows(InvalidTokenException.class, () -> jwtService.validateToken("invalid.token.string"));
    }

    @Test
    void testRefreshTokens_success() {
        String email = "test@example.com";

        String refreshToken = jwtService.generateRefreshToken(email, String.valueOf(UserRole.ROLE_USER));
        String tokenHash = ReflectionTestUtils.invokeMethod(jwtService, "hashToken", refreshToken);

        RefreshToken tokenEntity = new RefreshToken();
        tokenEntity.setTokenHash(tokenHash);

        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(email);
        userCredential.setRole(UserRole.ROLE_USER);
        tokenEntity.setUser(new UserCredential());

        when(refreshTokenRepository.findByTokenHashForUpdate(tokenHash)).thenReturn(Optional.of(tokenEntity));
        when(userCredentialRepository.findByEmail(email)).thenReturn(Optional.of(tokenEntity.getUser()));
        when(deviceIdExtractor.extract()).thenReturn("device-1");

        JwtResponse response = jwtService.refreshTokens(refreshToken);

        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        verify(refreshTokenRepository).delete(tokenEntity);
    }

    @Test
    void testRefreshTokens_invalidHash() {
        String token = jwtService.generateRefreshToken("test@example.com", "USER");
        String hash = ReflectionTestUtils.invokeMethod(jwtService, "hashToken", token);
        when(refreshTokenRepository.findByTokenHashForUpdate(hash)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> jwtService.refreshTokens(token));
    }

    @Test
    void testGenerateAndStoreRefreshToken_userNotFound() {
        when(userCredentialRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(deviceIdExtractor.extract()).thenReturn("device-1");

        assertThrows(UserNotFoundException.class, () ->
                jwtService.generateAndStoreRefreshToken("nonexistent@example.com", "USER"));
    }

    @Test
    void testDeleteRefreshToken() {
        String token = jwtService.generateRefreshToken("user@example.com", "USER");
        String hash = ReflectionTestUtils.invokeMethod(jwtService, "hashToken", token);

        jwtService.deleteRefreshToken(token);
        verify(refreshTokenRepository).deleteByTokenHash(hash);
    }

    @Test
    void testParseToken_expiredToken_shouldThrow() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "accessTokenExpirationMs", 1L);
        String token = jwtService.generateAccessToken("expired@example.com", "USER");
        Thread.sleep(5);

        assertThrows(TokenExpiredException.class, () -> jwtService.parseToken(token));
    }

    @Test
    void testExtractEmail() {
        String token = jwtService.generateAccessToken("user@example.com", "ADMIN");
        Claims claims = jwtService.parseToken(token);
        assertEquals("user@example.com", jwtService.extractEmail(claims));
    }

    @Test
    void testExtractRole() {
        String token = jwtService.generateAccessToken("user@example.com", "ADMIN");
        Claims claims = jwtService.parseToken(token);
        String role = ReflectionTestUtils.invokeMethod(jwtService, "extractRole", claims);
        assertEquals("ADMIN", role);
    }
}
