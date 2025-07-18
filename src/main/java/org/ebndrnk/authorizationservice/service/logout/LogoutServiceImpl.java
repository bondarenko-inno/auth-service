package org.ebndrnk.authorizationservice.service.logout;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.authorizationservice.exception.dto.token.InvalidTokenException;
import org.ebndrnk.authorizationservice.exception.dto.user.UserNotFoundException;
import org.ebndrnk.authorizationservice.model.dto.JwtRequest;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.ebndrnk.authorizationservice.repository.RefreshTokenRepository;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.ebndrnk.authorizationservice.service.jwt.JwtService;
import org.ebndrnk.authorizationservice.util.DeviceIdExtractor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final UserCredentialRepository userCredentialRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final DeviceIdExtractor deviceIdExtractor;
    private final JwtService jwtService;

    @Transactional
    @Override
    public void logout(JwtRequest request) {
        String email = extractEmailFromToken(request.token());
        String deviceId = deviceIdExtractor.extract();

        log.info("Processing logout for user with email: {}", email);

        UserCredential user = userCredentialRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found during logout: {}", email);
                    return new UserNotFoundException("User not found");
                });

        refreshTokenRepository.deleteByUserAndDeviceId(user, deviceId);
        log.info("All refresh tokens deleted for user {}", email);
    }


    private String extractEmailFromToken(String token) {
        if(validateToken(token)) {
            return jwtService.extractEmail(jwtService.parseToken(token));
        }else {
            throw new InvalidTokenException("Invalid token");
        }
    }

    private boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }



}
