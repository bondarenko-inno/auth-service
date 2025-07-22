package org.ebndrnk.authorizationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ebndrnk.authorizationservice.model.dto.AuthenticationRequest;
import org.ebndrnk.authorizationservice.model.dto.JwtResponse;
import org.ebndrnk.authorizationservice.service.authentication.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user authentication.
 * <p>
 * Provides an endpoint for authenticating users
 * and issuing JWT tokens upon successful login.
 */
@RestController
@RequestMapping("/authenticate")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and token issuance.")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Authenticates a user with provided credentials.
     *
     * @param authenticationRequest the authentication request containing email and password
     * @return a {@link ResponseEntity} with a {@link JwtResponse} containing access and refresh tokens
     */
    @PostMapping
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user with provided credentials and returns JWT tokens.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authentication successful",
                            content = @Content(schema = @Schema(implementation = JwtResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials"
                    )
            }
    )
    public ResponseEntity<JwtResponse> authenticate(
            @RequestBody AuthenticationRequest authenticationRequest
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }
}
