package org.ebndrnk.authorizationservice.service.registration;

import org.ebndrnk.authorizationservice.model.dto.registration.RegistrationRequest;
import org.ebndrnk.authorizationservice.model.dto.token.JwtResponse;

public interface RegistrationService {
    JwtResponse register(RegistrationRequest registrationRequest);
}
