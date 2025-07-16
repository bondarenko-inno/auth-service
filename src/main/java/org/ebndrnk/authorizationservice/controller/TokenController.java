package org.ebndrnk.authorizationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ebndrnk.authorizationservice.model.dto.JwtRequest;
import org.ebndrnk.authorizationservice.model.dto.JwtResponse;
import org.ebndrnk.authorizationservice.service.jwt.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling JWT token operations.
 * <p>
 * Provides endpoints for validating tokens and refreshing them.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "Tokens", description = "Endpoints for validating and refreshing JWT tokens.")
public class TokenController {

    private final JwtService jwtService;

    /**
     * Validates a provided JWT token.
     *
     * @param request the request containing the token to validate
     * @return 200 OK if valid, 401 Unauthorized otherwise
     */
    @PostMapping("/validate")
    @Operation(
            summary = "Validate JWT token",
            description = "Validates a provided JWT token and returns 200 OK if valid, or 401 Unauthorized otherwise.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token is valid"),
                    @ApiResponse(responseCode = "401", description = "Token is invalid or expired")
            }
    )
    public ResponseEntity<Void> validateToken(@RequestBody JwtRequest request) {
        boolean valid = jwtService.validateToken(request.token());
        if (valid) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Refreshes access and refresh tokens using a valid refresh token.
     *
     * @param request the request containing the refresh token
     * @return a {@link ResponseEntity} with new {@link JwtResponse} tokens
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh tokens",
            description = "Generates new access and refresh tokens using a valid refresh token.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tokens refreshed successfully",
                            content = @Content(schema = @Schema(implementation = JwtResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid or expired refresh token"
                    )
            }
    )
    public ResponseEntity<JwtResponse> refreshTokens(@RequestBody JwtRequest request) {
        JwtResponse response = jwtService.refreshTokens(request.token());
        return ResponseEntity.ok(response);
    }
}
