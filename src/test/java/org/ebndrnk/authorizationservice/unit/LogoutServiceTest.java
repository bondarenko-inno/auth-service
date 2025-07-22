package org.ebndrnk.authorizationservice.unit;

import org.ebndrnk.authorizationservice.exception.dto.token.InvalidTokenException;
import org.ebndrnk.authorizationservice.model.dto.JwtRequest;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.ebndrnk.authorizationservice.repository.RefreshTokenRepository;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.ebndrnk.authorizationservice.service.jwt.JwtService;
import org.ebndrnk.authorizationservice.service.logout.LogoutService;
import org.ebndrnk.authorizationservice.service.logout.LogoutServiceImpl;
import org.ebndrnk.authorizationservice.util.DeviceIdExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class LogoutServiceTest {

    private LogoutService logoutService;

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private DeviceIdExtractor deviceIdExtractor;

    @Mock
    private JwtService jwtService;

    private static final String TEST_EMAIL = "user@example.com";
    private static final String TOKEN = "valid.token.value";
    private static final String DEVICE_ID = "device-id";

    private final UserCredential user = new UserCredential();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user.setEmail(TEST_EMAIL);
        logoutService = new LogoutServiceImpl(userCredentialRepository, refreshTokenRepository, deviceIdExtractor, jwtService);
    }

    @Test
    void logout_shouldDeleteRefreshToken_forValidToken() {
        JwtRequest request = new JwtRequest(TOKEN);

        when(jwtService.validateToken(TOKEN)).thenReturn(true);
        when(jwtService.parseToken(TOKEN)).thenReturn(null); // just stub; we test extractEmail
        when(jwtService.extractEmail(null)).thenReturn(TEST_EMAIL);
        when(userCredentialRepository.findByEmail(TEST_EMAIL)).thenReturn(java.util.Optional.of(user));
        when(deviceIdExtractor.extract()).thenReturn(DEVICE_ID);

        logoutService.logout(request);

        verify(refreshTokenRepository).deleteByUserAndDeviceId(user, DEVICE_ID);
    }

    @Test
    void logout_shouldThrowInvalidTokenException_whenTokenInvalid() {
        JwtRequest request = new JwtRequest("invalid.token");

        when(jwtService.validateToken("invalid.token")).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> logoutService.logout(request));
        verifyNoInteractions(deviceIdExtractor, userCredentialRepository, refreshTokenRepository);
    }

}
