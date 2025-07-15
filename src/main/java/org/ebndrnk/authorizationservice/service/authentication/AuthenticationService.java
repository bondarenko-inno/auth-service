package org.ebndrnk.authorizationservice.service.authentication;

import org.ebndrnk.authorizationservice.model.dto.authentication.AuthenticationRequest;
import org.ebndrnk.authorizationservice.model.dto.token.JwtResponse;

public interface AuthenticationService {
    JwtResponse authenticate(AuthenticationRequest authenticationRequest);
}
