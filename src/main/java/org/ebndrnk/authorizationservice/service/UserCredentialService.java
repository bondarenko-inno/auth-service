package org.ebndrnk.authorizationservice.service;

import lombok.RequiredArgsConstructor;
import org.ebndrnk.authorizationservice.exception.user.UserNotFoundException;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCredentialService {
    private final UserCredentialRepository userCredentialRepository;

    public boolean isExistsByEmail(String email) {
        return userCredentialRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void deleteByEmail(String email) {
        if(!isExistsByEmail(email)) {
            throw new UserNotFoundException("User not found for email " + email);
        }
        userCredentialRepository.deleteByEmail(email);
    }
}
