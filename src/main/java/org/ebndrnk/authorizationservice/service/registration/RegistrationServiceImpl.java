package org.ebndrnk.authorizationservice.service.registration;

import lombok.RequiredArgsConstructor;
import org.ebndrnk.authorizationservice.model.dto.registration.RegistrationRequest;
import org.ebndrnk.authorizationservice.model.dto.token.JwtResponse;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.ebndrnk.authorizationservice.model.entity.user.UserRole;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.ebndrnk.authorizationservice.service.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public JwtResponse register(RegistrationRequest registrationRequest) {
        String hashedPassword = passwordEncoder.encode(registrationRequest.password());

        UserCredential userCredential = new UserCredential();
        userCredential.setEmail(registrationRequest.email());
        userCredential.setPasswordHash(hashedPassword);
        userCredential.setRole(UserRole.ROLE_USER);

        userCredentialRepository.save(userCredential);

        String accessToken = jwtService.generateAccessToken(
                userCredential.getEmail(),
                userCredential.getRole().name()
        );

        String refreshToken = jwtService.generateRefreshToken(userCredential.getEmail());

        return new JwtResponse(accessToken, refreshToken);
    }
}
