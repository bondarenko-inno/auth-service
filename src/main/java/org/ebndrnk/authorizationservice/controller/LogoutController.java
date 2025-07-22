package org.ebndrnk.authorizationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ebndrnk.authorizationservice.model.dto.JwtRequest;
import org.ebndrnk.authorizationservice.service.logout.LogoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for user logout operations.
 * <p>
 * Provides an endpoint to remove refresh tokens
 * and effectively log a user out from the system.
 */
@RestController
@RequestMapping("/logout")
@RequiredArgsConstructor
@Tag(name = "Logout", description = "Endpoints for logging users out and clearing tokens.")
public class LogoutController {

    private final LogoutService logoutService;

    /**
     * Logs out the current user by deleting all their refresh tokens.
     *
     * @param request the HTTP request containing headers for extracting authentication info
     * @return an empty {@link ResponseEntity} indicating success
     */
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @Operation(
            summary = "Logout user",
            description = "Deletes refresh tokens for the current user and current device and logs them out.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Logout successful")
            }
    )
    public ResponseEntity<Void> logout(@Valid @RequestBody JwtRequest request) {
        logoutService.logout(request);
        return ResponseEntity.ok().build();
    }
}
