package org.ebndrnk.authorizationservice.repository;

import jakarta.persistence.LockModeType;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.ebndrnk.authorizationservice.model.entity.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteAllByUser(UserCredential user);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RefreshToken r WHERE r.tokenHash = :hash")
    Optional<RefreshToken> findByTokenHashForUpdate(@Param("hash") String hash);


    void deleteByTokenHash(String tokenHash);

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user AND rt.deviceId = :deviceId")
    void deleteByUserAndDeviceId(@Param("user") UserCredential user,
                                 @Param("deviceId") String deviceId);

}
