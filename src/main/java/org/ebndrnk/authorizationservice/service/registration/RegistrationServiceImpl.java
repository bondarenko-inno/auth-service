package org.ebndrnk.authorizationservice.service.registration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.authorizationservice.exception.dto.user.DuplicateEmailException;
import org.ebndrnk.authorizationservice.model.dto.RegistrationRequest;
import org.ebndrnk.authorizationservice.model.dto.JwtResponse;
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

    @Override
    @Transactional
    public JwtResponse register(RegistrationRequest registrationRequest) {
        log.info("Registering new user with email: {}", registrationRequest.email());

        if (userCredentialRepository.findByEmail(registrationRequest.email()).isPresent()) {
            log.warn("Attempt to register duplicate email: {}", registrationRequest.email());
            throw new DuplicateEmailException("Email already exists: " + registrationRequest.email());
        }

        String hashedPassword = passwordEncoder.encode(registrationRequest.password());

        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(registrationRequest.email());
        userCredential.setPasswordHash(hashedPassword);
        userCredential.setRole(UserRole.ROLE_USER);

        userCredentialRepository.save(userCredential);
        log.info("User {} successfully saved", registrationRequest.email());

        String accessToken = jwtService.generateAccessToken(
                userCredential.getEmail(),
                userCredential.getRole().name()
        );

        String refreshToken = jwtService.generateAndStoreRefreshToken(
                userCredential.getEmail(),
                userCredential.getRole().name()
        );

        log.info("Access and refresh tokens generated for user {}", registrationRequest.email());

        return new JwtResponse(accessToken, refreshToken);
    }
}
