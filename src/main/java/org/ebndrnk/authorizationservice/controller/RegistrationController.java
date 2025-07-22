    package org.ebndrnk.authorizationservice.controller;

    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.media.Content;
    import io.swagger.v3.oas.annotations.media.Schema;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import lombok.RequiredArgsConstructor;
    import org.ebndrnk.authorizationservice.model.dto.RegistrationRequest;
    import org.ebndrnk.authorizationservice.model.dto.JwtResponse;
    import org.ebndrnk.authorizationservice.service.registration.RegistrationService;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    /**
     * REST controller for user registration.
     * <p>
     * Handles creating new user accounts and issuing JWT tokens upon successful registration.
     */
    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/register")
    @Tag(name = "Registration", description = "Endpoints for user registration and token issuance.")
    public class RegistrationController {

        private final RegistrationService registrationService;

        /**
         * Registers a new user account.
         *
         * @param registrationRequest the registration details (email, password, etc.)
         * @return a {@link ResponseEntity} with a {@link JwtResponse} containing access and refresh tokens
         */
        @PostMapping
        @Operation(
                summary = "Register new user",
                description = "Registers a new user and returns JWT tokens for authentication.",
                responses = {
                        @ApiResponse(
                                responseCode = "201",
                                description = "Registration successful",
                                content = @Content(schema = @Schema(implementation = JwtResponse.class))
                        ),
                        @ApiResponse(
                                responseCode = "409",
                                description = "Email already exists"
                        )
                }
        )
        public ResponseEntity<JwtResponse> register(
                @RequestBody RegistrationRequest registrationRequest
        ) {
            return ResponseEntity.status(HttpStatus.CREATED).body(registrationService.register(registrationRequest));
        }
    }
