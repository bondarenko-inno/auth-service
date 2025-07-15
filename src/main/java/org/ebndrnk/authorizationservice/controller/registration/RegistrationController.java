package org.ebndrnk.authorizationservice.controller.registration;

import lombok.RequiredArgsConstructor;
import org.ebndrnk.authorizationservice.model.dto.registration.RegistrationRequest;
import org.ebndrnk.authorizationservice.model.dto.token.JwtResponse;
import org.ebndrnk.authorizationservice.service.registration.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegistrationController {

    private final RegistrationService registrationService;


    @PostMapping
    public ResponseEntity<JwtResponse> register(RegistrationRequest registrationRequest) {
        return ResponseEntity.ok(registrationService.register(registrationRequest));
    }
}
