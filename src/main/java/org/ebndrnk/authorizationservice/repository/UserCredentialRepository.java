package org.ebndrnk.authorizationservice.repository;

import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    Optional<UserCredential> findByEmail(String email);

    void deleteByEmail(String email);
}
