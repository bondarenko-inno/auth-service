package org.ebndrnk.authorizationservice.controller.authentication;

import lombok.RequiredArgsConstructor;
import org.ebndrnk.authorizationservice.model.dto.authentication.AuthenticationRequest;
import org.ebndrnk.authorizationservice.model.dto.token.JwtResponse;
import org.ebndrnk.authorizationservice.service.authentication.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authenticate")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<JwtResponse> register(AuthenticationRequest authenticationRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }
}

