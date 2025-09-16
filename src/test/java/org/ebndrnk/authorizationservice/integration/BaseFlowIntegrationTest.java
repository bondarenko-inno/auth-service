package org.ebndrnk.authorizationservice.integration;

import org.ebndrnk.authorizationservice.config.TestContainersConfig;
import org.ebndrnk.authorizationservice.model.dto.AuthenticationRequest;
import org.ebndrnk.authorizationservice.model.dto.JwtRequest;
import org.ebndrnk.authorizationservice.model.dto.JwtResponse;
import org.ebndrnk.authorizationservice.model.dto.RegistrationRequest;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.ebndrnk.authorizationservice.model.entity.token.RefreshToken;
import org.ebndrnk.authorizationservice.repository.RefreshTokenRepository;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BaseFlowIntegrationTest extends TestContainersConfig {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @BeforeEach
    void cleanDb() {
        refreshTokenRepository.deleteAll();
        userCredentialRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegistrationRequest request = new RegistrationRequest(
                "testuser@example.com", "Password123",
                LocalDateTime.of(1990, 1, 1, 0, 0), "John", "Doe"
        );

        ResponseEntity<Void> response = restTemplate.postForEntity(getBaseUrl() + "/register", request, Void.class);

        assertEquals(CREATED, response.getStatusCode());

        Optional<UserCredential> saved = userCredentialRepository.findByEmail("testuser@example.com");
        assertTrue(saved.isPresent());
    }


    @Test
    @Transactional
    void shouldAuthenticateSuccessfully() {
        String email = "auth@example.com";
        String password = "Secret123";

        RegistrationRequest registrationRequest = new RegistrationRequest(
                email,
                password,
                LocalDateTime.now(),
                "Test",
                "User"
        );

        ResponseEntity<JwtResponse> registerResponse = restTemplate.postForEntity(
                getBaseUrl() + "/register",
                registrationRequest,
                JwtResponse.class
        );

        assertEquals(CREATED, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());
        assertNotNull(registerResponse.getBody().accessToken());
        assertNotNull(registerResponse.getBody().refreshToken());

        AuthenticationRequest loginRequest = new AuthenticationRequest(email, password);
        ResponseEntity<JwtResponse> authResponse = restTemplate.postForEntity(
                getBaseUrl() + "/authenticate",
                loginRequest,
                JwtResponse.class
        );

        assertEquals(OK, authResponse.getStatusCode());
        assertNotNull(authResponse.getBody());
        assertNotNull(authResponse.getBody().accessToken());
        assertNotNull(authResponse.getBody().refreshToken());

        assertFalse(refreshTokenRepository.findAll().isEmpty(), "Refresh token should be stored");
    }



    @Test
    void shouldValidateAccessTokenSuccessfully() {
        String email = "valid@example.com";
        String accessToken = loginAndGetToken(email).accessToken();

        JwtRequest request = new JwtRequest(accessToken);
        ResponseEntity<Void> response = restTemplate.postForEntity(getBaseUrl() + "/validate", request, Void.class);

        assertEquals(OK, response.getStatusCode());
    }

    @Test
    void shouldRefreshTokens() {
        String email = "refresh@example.com";
        JwtResponse tokens = loginAndGetToken(email);

        JwtRequest request = new JwtRequest(tokens.refreshToken());
        ResponseEntity<JwtResponse> response = restTemplate.postForEntity(getBaseUrl() + "/refresh", request, JwtResponse.class);

        assertEquals(OK, response.getStatusCode());
        assertNotNull(response.getBody().accessToken());
        assertNotNull(response.getBody().refreshToken());
    }


    @Test
    void shouldLogoutSuccessfully() {
        cleanDb();
        JwtResponse tokens = loginAndGetToken("testuser1@example.com");
        JwtRequest logoutRequest = new JwtRequest(tokens.accessToken());

        ResponseEntity<Void> response = restTemplate.postForEntity(getBaseUrl() + "/logout", logoutRequest, Void.class);

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAll();

        assertEquals(OK, response.getStatusCode());
        assertEquals(0, refreshTokens.size());

    }

    protected JwtResponse loginAndGetToken(String email) {
        String password = "Secret123";

        RegistrationRequest registrationRequest = new RegistrationRequest(
                email,
                password,
                LocalDateTime.now(),
                "Test",
                "User"
        );

        ResponseEntity<JwtResponse> registerResponse = restTemplate.postForEntity(
                getBaseUrl() + "/register",
                registrationRequest,
                JwtResponse.class
        );

        assertEquals(CREATED, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());
        assertNotNull(registerResponse.getBody().accessToken());
        assertNotNull(registerResponse.getBody().refreshToken());

        AuthenticationRequest request = new AuthenticationRequest(email, password);
        ResponseEntity<JwtResponse> response = restTemplate.postForEntity(getBaseUrl() + "/authenticate", request, JwtResponse.class);
        System.out.println("Response: " + response.getBody());
        return response.getBody();
    }
} 