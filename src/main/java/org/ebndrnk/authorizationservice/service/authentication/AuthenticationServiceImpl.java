package org.ebndrnk.authorizationservice.service.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.authorizationservice.exception.token.TokenGenerationException;
import org.ebndrnk.authorizationservice.exception.user.InvalidCredentialsException;
import org.ebndrnk.authorizationservice.exception.user.UserNotFoundException;
import org.ebndrnk.authorizationservice.model.dto.AuthenticationRequest;
import org.ebndrnk.authorizationservice.model.dto.JwtResponse;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.ebndrnk.authorizationservice.service.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public JwtResponse authenticate(AuthenticationRequest authenticationRequest) {
        log.info("Authenticating user with email: {}", authenticationRequest.email());

        UserCredential userCredential = userCredentialRepository.findByEmail(authenticationRequest.email())
                .orElseThrow(() -> {
                    log.warn("User with email {} not found", authenticationRequest.email());
                    return new UserNotFoundException(
                            "User with email %s not found".formatted(authenticationRequest.email()));
                });

        if (!passwordEncoder.matches(authenticationRequest.password(), userCredential.getPasswordHash())) {
            log.warn("Invalid credentials for user with email: {}", authenticationRequest.email());
            throw new InvalidCredentialsException("Invalid credentials");
        }

        try {
            String accessToken = jwtService.generateAccessToken(
                    userCredential.getEmail(),
                    userCredential.getRole().name()
            );
            String refreshToken = jwtService.generateAndStoreRefreshToken(
                    userCredential.getEmail(),
                    userCredential.getRole().name()
            );
            log.info("User {} authenticated successfully", authenticationRequest.email());
            return new JwtResponse(accessToken, refreshToken);
        } catch (Exception e) {
            log.error("Failed to generate tokens for user {}: {}", authenticationRequest.email(), e.getMessage(), e);
            throw new TokenGenerationException("Failed to generate tokens");
        }
    }
}
