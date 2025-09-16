package org.ebndrnk.authorizationservice.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ebndrnk.authorizationservice.exception.token.InvalidTokenException;
import org.ebndrnk.authorizationservice.exception.token.TokenExpiredException;
import org.ebndrnk.authorizationservice.exception.token.TokenParsingException;
import org.ebndrnk.authorizationservice.exception.user.UserNotFoundException;
import org.ebndrnk.authorizationservice.model.dto.JwtResponse;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.ebndrnk.authorizationservice.model.entity.token.RefreshToken;
import org.ebndrnk.authorizationservice.repository.RefreshTokenRepository;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.ebndrnk.authorizationservice.util.DeviceIdExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final DeviceIdExtractor deviceIdExtractor;

    @Value("${secret.key}")
    private String secret;

    @Value("${secret.ttl.access}")
    private long accessTokenExpirationMs;

    @Value("${secret.ttl.refresh}")
    private long refreshTokenExpirationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserCredentialRepository userCredentialRepository;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        log.info("JWT signing key initialized");
    }

    /**
     * Generates an access token containing email and role claims.
     *
     * @param email user's email
     * @param role  user's role
     * @return signed JWT access token string
     */
    public String generateAccessToken(String email, String role) {
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("type", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        log.debug("Generated access token for user {}", email);
        return token;
    }

    /**
     * Generates a refresh token containing email and role claims.
     *
     * @param email user's email
     * @param role  user's role
     * @return signed JWT refresh token string
     */
    public String generateRefreshToken(String email, String role) {
        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        log.debug("Generated refresh token for user {}", email);
        return token;
    }

    /**
     * Generates a refresh token, hashes it, and stores it associated with user and device.
     *
     * @param email user's email
     * @param role  user's role
     * @return generated raw refresh token string
     */
    public String generateAndStoreRefreshToken(String email, String role) {
        String token = generateRefreshToken(email, role);
        String hash = hashToken(token);
        Date expiration = extractExpiration(token);
        String deviceId = deviceIdExtractor.extract();

        UserCredential user = userCredentialRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found for refresh token generation: {}", email);
                    return new UserNotFoundException("User not found for refresh token");
                });

        refreshTokenRepository.deleteByUserAndDeviceId(user, deviceId);
        refreshTokenRepository.flush();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setTokenHash(hash);
        refreshToken.setExpiresAt(expiration);
        refreshToken.setDeviceId(deviceId);
        refreshToken.setUser(user);

        refreshTokenRepository.save(refreshToken);
        log.info("Stored refresh token for user {} on device {}", email, deviceId);

        return token;
    }

    /**
     * Validates the given JWT token.
     *
     * @param token JWT token string
     * @return true if token is valid
     * @throws InvalidTokenException    if token is invalid
     * @throws TokenExpiredException    if token is expired
     * @throws TokenParsingException    if token cannot be parsed
     */
    public boolean validateToken(String token) {
        parseToken(token);
        log.debug("Validated token successfully");
        return true;
    }

    /**
     * Refreshes JWT access and refresh tokens using a valid refresh token.
     * Deletes old refresh token and stores a new one.
     *
     * @param refreshToken the current refresh token string
     * @return new {@link JwtResponse} containing fresh access and refresh tokens
     */
    @Transactional
    public synchronized JwtResponse refreshTokens(String refreshToken) {
        Claims claims = parseToken(refreshToken);

        assertIsRefreshToken(claims);
        assertNotExpired(claims);

        String hash = hashToken(refreshToken);
        RefreshToken tokenEntity = refreshTokenRepository.findByTokenHashForUpdate(hash)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found or revoked: {}", hash);
                    return new InvalidTokenException("Refresh token not found or already revoked.");
                });

        String email = extractEmail(claims);
        String role = extractRole(claims);

        refreshTokenRepository.delete(tokenEntity);
        log.info("Deleted old refresh token for user {}", email);

        String newAccessToken = generateAccessToken(email, role);
        String newRefreshToken = generateAndStoreRefreshToken(email, role);
        log.info("Refreshed tokens for user {}", email);

        return new JwtResponse(newAccessToken, newRefreshToken);
    }

    /**
     * Deletes a refresh token by its raw token string.
     *
     * @param refreshToken the refresh token string to delete
     */
    public void deleteRefreshToken(String refreshToken) {
        String hash = hashToken(refreshToken);
        refreshTokenRepository.deleteByTokenHash(hash);
        log.info("Deleted refresh token with hash {}", hash);
    }

    /**
     * Hashes token string using SHA-256.
     *
     * @param token the raw token string
     * @return hex-encoded hash string
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("SHA-256 not supported", e);
        }
    }

    /**
     * Extracts expiration date from token claims.
     *
     * @param token JWT token string
     * @return expiration date
     */
    private Date extractExpiration(String token) {
        return parseToken(token).getExpiration();
    }

    /**
     * Validates that the token type is 'refresh'.
     *
     * @param claims JWT claims
     */
    private void assertIsRefreshToken(Claims claims) {
        String type = claims.get("type", String.class);
        if (!"refresh".equals(type)) {
            log.error("Token is not a refresh token, type: {}", type);
            throw new InvalidTokenException("Provided token is not a refresh token.");
        }
    }

    /**
     * Checks if the token has expired.
     *
     * @param claims JWT claims
     */
    private void assertNotExpired(Claims claims) {
        if (claims.getExpiration().before(new Date())) {
            log.error("Token has expired at {}", claims.getExpiration());
            throw new TokenExpiredException("Token has expired.");
        }
    }

    /**
     * Extracts email (subject) from token claims.
     *
     * @param claims JWT claims
     * @return email string
     */
    public String extractEmail(Claims claims) {
        return claims.getSubject();
    }

    /**
     * Extracts role from token claims.
     *
     * @param claims JWT claims
     * @return role string
     * @throws TokenParsingException if role claim is missing
     */
    private String extractRole(Claims claims) {
        String role = claims.get("role", String.class);
        if (role == null) {
            log.error("Role claim is missing in token");
            throw new TokenParsingException("Role claim is missing in token.");
        }
        return role;
    }

    /**
     * Parses JWT token string and returns claims.
     * Throws appropriate exceptions for invalid or expired tokens.
     *
     * @param token JWT token string
     * @return Claims extracted from token
     */
    public Claims parseToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            Claims claims = jws.getBody();

            Date expiration = claims.getExpiration();
            if (expiration.before(new Date())) {
                log.error("Token expired at {}", expiration);
                throw new TokenExpiredException("Token has expired.");
            }

            return claims;

        } catch (ExpiredJwtException e) {
            log.error("JWT expired: {}", e.getMessage(), e);
            throw new TokenExpiredException("Token has expired.");
        } catch (JwtException e) {
            log.error("JWT parsing error: {}", e.getMessage(), e);
            throw new InvalidTokenException("Invalid JWT token: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error parsing JWT: {}", e.getMessage(), e);
            throw new TokenParsingException("Unexpected error parsing token.");
        }
    }
}
