package org.ebndrnk.authorizationservice.unit;

import org.ebndrnk.authorizationservice.exception.dto.user.DuplicateEmailException;
import org.ebndrnk.authorizationservice.kafka.UserEventPublisher;
import org.ebndrnk.authorizationservice.kafka.dto.UserCreatedEvent;
import org.ebndrnk.authorizationservice.model.dto.JwtResponse;
import org.ebndrnk.authorizationservice.model.dto.RegistrationRequest;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.ebndrnk.authorizationservice.model.entity.user.UserRole;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.ebndrnk.authorizationservice.service.jwt.JwtService;
import org.ebndrnk.authorizationservice.service.registration.RegistrationService;
import org.ebndrnk.authorizationservice.service.registration.RegistrationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegistrationServiceImplTest {

    private RegistrationService registrationService;

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserEventPublisher userEventPublisher;

    private final String email = "test@example.com";
    private final String password = "secure";
    private final String hashedPassword = "hashed";
    private final String name = "John";
    private final String surname = "Doe";
    private final LocalDateTime birthDate = LocalDateTime.of(2000, 1, 1, 1, 1, 1);

    private RegistrationRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        registrationService = new RegistrationServiceImpl(userCredentialRepository, passwordEncoder, jwtService, userEventPublisher);

        request = new RegistrationRequest(email, password, birthDate, name, surname);
    }

    @Test
    void register_shouldRegisterUserSuccessfully() {
        UserCredential savedUser = new UserCredential();
        savedUser.setEmail(email);
        savedUser.setPasswordHash(hashedPassword);
        savedUser.setRole(UserRole.ROLE_USER);

        when(userCredentialRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);
        when(userCredentialRepository.save(any(UserCredential.class))).thenReturn(savedUser);
        when(jwtService.generateAccessToken(email, "ROLE_USER")).thenReturn("access-token");
        when(jwtService.generateAndStoreRefreshToken(email, "ROLE_USER")).thenReturn("refresh-token");

        JwtResponse response = registrationService.register(request);

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());

        verify(userCredentialRepository).save(any(UserCredential.class));
        verify(userEventPublisher).publishUserCreated(any(UserCreatedEvent.class));
        verify(jwtService).generateAccessToken(email, "ROLE_USER");
        verify(jwtService).generateAndStoreRefreshToken(email, "ROLE_USER");
    }

    @Test
    void register_shouldThrowDuplicateEmailException_whenEmailExists() {
        when(userCredentialRepository.findByEmail(email)).thenReturn(Optional.of(new UserCredential()));

        assertThrows(DuplicateEmailException.class, () -> registrationService.register(request));

        verify(userCredentialRepository, never()).save(any());
        verify(userEventPublisher, never()).publishUserCreated(any());
        verify(jwtService, never()).generateAccessToken(any(), any());
    }
}
