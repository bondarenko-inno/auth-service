package org.ebndrnk.authorizationservice.service.logout;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.authorizationservice.exception.dto.user.UserNotFoundException;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.ebndrnk.authorizationservice.repository.RefreshTokenRepository;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.ebndrnk.authorizationservice.service.jwt.JwtService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutServiceImpl implements LogoutService {

    private final JwtService jwtService;
    private final UserCredentialRepository userCredentialRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public void logout(HttpServletRequest request) {
        String token = extractTokenFromHeader(request);
        String email = jwtService.extractEmail(jwtService.parseToken(token));
        log.info("Processing logout for user with email: {}", email);

        UserCredential user = userCredentialRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found during logout: {}", email);
                    return new UserNotFoundException("User not found");
                });

        refreshTokenRepository.deleteAllByUser(user);
        log.info("All refresh tokens deleted for user {}", email);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            log.error("Missing or invalid Authorization header");
            throw new RuntimeException("Missing Authorization header");
        }
        return header.substring(7);
    }
}
