package org.ebndrnk.authorizationservice.unit;

import org.ebndrnk.authorizationservice.exception.dto.token.TokenGenerationException;
import org.ebndrnk.authorizationservice.exception.dto.user.InvalidCredentialsException;
import org.ebndrnk.authorizationservice.exception.dto.user.UserNotFoundException;
import org.ebndrnk.authorizationservice.model.dto.AuthenticationRequest;
import org.ebndrnk.authorizationservice.model.dto.JwtResponse;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.ebndrnk.authorizationservice.model.entity.user.UserRole;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.ebndrnk.authorizationservice.service.authentication.AuthenticationService;
import org.ebndrnk.authorizationservice.service.authentication.AuthenticationServiceImpl;
import org.ebndrnk.authorizationservice.service.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AuthenticationServiceImpl}.
 * <p>
 * Tests cover the authentication flow including user lookup, password validation,
 * and token generation scenarios.
 */
class AuthenticationServiceTest {

    private UserCredentialRepository userCredentialRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationService authenticationService;

    private final String testEmail = "test@example.com";
    private final String testPassword = "password123";
    private final String encodedPassword = "$2a$10$encodedPasswordHash";
    private UserCredential testUser;

    /**
     * Initializes mocks and test data before each test.
     */
    @BeforeEach
    void setUp() {
        userCredentialRepository = mock(UserCredentialRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authenticationService = new AuthenticationServiceImpl(
                userCredentialRepository,
                passwordEncoder,
                jwtService
        );
        UserCredential testUser = new UserCredential();
        testUser.setEmail(testEmail);
        testUser.setPasswordHash(encodedPassword);
        testUser.setRole(UserRole.ROLE_USER);

        this.testUser = testUser;
    }

    /**
     * Verifies that {@link AuthenticationServiceImpl#authenticate} returns valid JWT tokens
     * when credentials are correct.
     */
    @Test
    void authenticate_shouldReturnJwtResponse_whenCredentialsValid() {
        AuthenticationRequest request = new AuthenticationRequest(testEmail, testPassword);
        JwtResponse expectedResponse = new JwtResponse("accessToken", "refreshToken");

        when(userCredentialRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(true);
        when(jwtService.generateAccessToken(testEmail, String.valueOf(UserRole.ROLE_USER))).thenReturn("accessToken");
        when(jwtService.generateAndStoreRefreshToken(testEmail, String.valueOf(UserRole.ROLE_USER))).thenReturn("refreshToken");

        JwtResponse response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertEquals(expectedResponse.accessToken(), response.accessToken());
        assertEquals(expectedResponse.refreshToken(), response.refreshToken());
        verify(userCredentialRepository, times(1)).findByEmail(testEmail);
        verify(passwordEncoder, times(1)).matches(testPassword, encodedPassword);
        verify(jwtService, times(1)).generateAccessToken(testEmail, String.valueOf(UserRole.ROLE_USER));
        verify(jwtService, times(1)).generateAndStoreRefreshToken(testEmail, String.valueOf(UserRole.ROLE_USER));
    }

    /**
     * Verifies that {@link AuthenticationServiceImpl#authenticate} throws {@link UserNotFoundException}
     * when user email is not found.
     */
    @Test
    void authenticate_shouldThrowUserNotFoundException_whenUserNotFound() {
        String nonExistentEmail = "nonexistent@example.com";
        AuthenticationRequest request = new AuthenticationRequest(nonExistentEmail, testPassword);

        when(userCredentialRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                authenticationService.authenticate(request)
        );
        verify(userCredentialRepository, times(1)).findByEmail(nonExistentEmail);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtService);
    }

    /**
     * Verifies that {@link AuthenticationServiceImpl#authenticate} throws {@link InvalidCredentialsException}
     * when password is incorrect.
     */
    @Test
    void authenticate_shouldThrowInvalidCredentialsException_whenPasswordInvalid() {
        AuthenticationRequest request = new AuthenticationRequest(testEmail, "wrongPassword");

        when(userCredentialRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", encodedPassword)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () ->
                authenticationService.authenticate(request)
        );
        verify(passwordEncoder, times(1)).matches("wrongPassword", encodedPassword);
        verifyNoInteractions(jwtService);
    }

    /**
     * Verifies that {@link AuthenticationServiceImpl#authenticate} throws {@link TokenGenerationException}
     * when token generation fails.
     */
    @Test
    void authenticate_shouldThrowTokenGenerationException_whenTokenGenerationFails() {
        AuthenticationRequest request = new AuthenticationRequest(testEmail, testPassword);

        when(userCredentialRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(true);
        when(jwtService.generateAccessToken(testEmail, String.valueOf(UserRole.ROLE_USER))).thenThrow(new RuntimeException("Token error"));

        assertThrows(TokenGenerationException.class, () ->
                authenticationService.authenticate(request)
        );
        verify(jwtService, times(1)).generateAccessToken(testEmail, String.valueOf(UserRole.ROLE_USER));
    }

    /**
     * Verifies that {@link AuthenticationServiceImpl#authenticate} uses correct role
     * when generating tokens for ADMIN users.
     */
    @Test
    void authenticate_shouldUseAdminRole_whenUserIsAdmin() {
        testUser.setRole(UserRole.ROLE_ADMIN);
        AuthenticationRequest request = new AuthenticationRequest(testEmail, testPassword);

        when(userCredentialRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(true);
        when(jwtService.generateAccessToken(testEmail, String.valueOf(UserRole.ROLE_ADMIN))).thenReturn("adminToken");
        when(jwtService.generateAndStoreRefreshToken(testEmail, String.valueOf(UserRole.ROLE_ADMIN))).thenReturn("refreshToken");

        JwtResponse response = authenticationService.authenticate(request);

        assertEquals("adminToken", response.accessToken());
        verify(jwtService, times(1)).generateAccessToken(testEmail, String.valueOf(UserRole.ROLE_ADMIN));
    }
}