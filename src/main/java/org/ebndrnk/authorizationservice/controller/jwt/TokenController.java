package org.ebndrnk.authorizationservice.controller.jwt;

import lombok.RequiredArgsConstructor;
import org.ebndrnk.authorizationservice.model.dto.token.JwtResponse;
import org.ebndrnk.authorizationservice.service.jwt.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TokenController {

    private final JwtService jwtService;


    //TODO стэк трейс причин 401-ой
    @PostMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestBody String token) {
        boolean valid = jwtService.validateToken(token);
        if (valid) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refreshTokens(@RequestBody String refreshToken) {
        if (!jwtService.validateRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = jwtService.extractEmail(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(email, jwtService.extractRole(refreshToken));
        String newRefreshToken = jwtService.generateRefreshToken(email);

        JwtResponse response = new JwtResponse(newAccessToken, newRefreshToken);
        return ResponseEntity.ok(response);
    }
}
