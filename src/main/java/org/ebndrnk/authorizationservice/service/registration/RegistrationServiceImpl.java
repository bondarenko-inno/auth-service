package org.ebndrnk.authorizationservice.service.registration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.authorizationservice.kafka.UserEventPublisher;
import org.ebndrnk.authorizationservice.exception.user.DuplicateEmailException;
import org.ebndrnk.authorizationservice.model.dto.JwtResponse;
import org.ebndrnk.authorizationservice.model.dto.RegistrationRequest;
import org.ebndrnk.authorizationservice.kafka.dto.UserCreatedEvent;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.ebndrnk.authorizationservice.model.entity.user.UserRole;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.ebndrnk.authorizationservice.service.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserEventPublisher userEventPublisher;

    @Override
    @Transactional
    public JwtResponse register(RegistrationRequest registrationRequest) {
        log.info("Starting user registration for email: {}", registrationRequest.email());

        checkEmailDuplicate(registrationRequest.email());
        UserCredential savedUser = saveUserCredential(registrationRequest);
        publishUserCreatedEvent(registrationRequest);
        JwtResponse jwtResponse = generateJwtTokens(savedUser);

        log.info("Registration process completed successfully for user: {}", registrationRequest.email());
        return jwtResponse;
    }

    private void checkEmailDuplicate(String email) {
        userCredentialRepository.findByEmail(email).ifPresent(existingUser -> {
            log.warn("Attempt to register duplicate email: {}", email);
            throw new DuplicateEmailException("Email already exists: " + email);
        });
    }

    private UserCredential saveUserCredential(RegistrationRequest request) {
        String hashedPassword = passwordEncoder.encode(request.password());

        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(request.email());
        userCredential.setPasswordHash(hashedPassword);
        userCredential.setRole(UserRole.ROLE_USER);

        UserCredential savedUser = userCredentialRepository.save(userCredential);
        log.info("UserCredential saved successfully for email: {}", request.email());

        return savedUser;
    }

    private void publishUserCreatedEvent(RegistrationRequest request) {
        UserCreatedEvent event = new UserCreatedEvent(
                request.email(),
                request.birthDate(),
                request.name(),
                request.surname()
        );
        userEventPublisher.publishUserCreated(event);
        log.info("UserCreatedEvent published for email: {}", request.email());
    }

    private JwtResponse generateJwtTokens(UserCredential userCredential) {
        String accessToken = jwtService.generateAccessToken(
                userCredential.getEmail(),
                userCredential.getRole().name()
        );

        String refreshToken = jwtService.generateAndStoreRefreshToken(
                userCredential.getEmail(),
                userCredential.getRole().name()
        );

        log.info("JWT tokens generated for user: {}", userCredential.getEmail());
        return new JwtResponse(accessToken, refreshToken);
    }
}
