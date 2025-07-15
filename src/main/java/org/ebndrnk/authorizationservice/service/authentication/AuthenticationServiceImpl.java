package org.ebndrnk.authorizationservice.service.authentication;

import lombok.RequiredArgsConstructor;
import org.ebndrnk.authorizationservice.model.dto.authentication.AuthenticationRequest;
import org.ebndrnk.authorizationservice.model.dto.token.JwtResponse;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.ebndrnk.authorizationservice.service.jwt.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public JwtResponse authenticate(AuthenticationRequest authenticationRequest) {
        UserCredential userCredential = userCredentialRepository.findByEmail(authenticationRequest.email())
                .orElseThrow(() -> new RuntimeException("User not found")); //TODO custom exceptions

        if (!passwordEncoder.matches(authenticationRequest.password(), userCredential.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials"); //TODO
        }

        String accessToken = jwtService.generateAccessToken(userCredential.getEmail(), userCredential.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(userCredential.getEmail());

        return new JwtResponse(accessToken, refreshToken);
    }
}
