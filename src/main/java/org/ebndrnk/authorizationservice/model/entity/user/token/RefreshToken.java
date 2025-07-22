package org.ebndrnk.authorizationservice.model.entity.user.token;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.ebndrnk.authorizationservice.model.entity.BasicEntity;
import org.ebndrnk.authorizationservice.model.entity.user.UserCredential;
import org.hibernate.annotations.Comment;

import java.util.Date;

/**
 * Entity representing a refresh token issued for a specific user and device.
 * <p>
 * Stores hashed token, expiration date, device identifier, and associated user.
 */
@Entity
@Table(
        name = "refresh_tokens",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "device_id"}
        )
)
@Getter
@Setter
public class RefreshToken extends BasicEntity {

    /**
     * Hashed refresh token value.
     */
    @Comment("Hashed refresh token value.")
    @Column(name = "token_hash", nullable = false, unique = true, length = 128)
    private String tokenHash;

    /**
     * Expiration date and time of the refresh token.
     */
    @Comment("Expiration date and time of the refresh token.")
    @Column(name = "expires_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiresAt;

    /**
     * Unique identifier of the device where the token is used (e.g. user agent).
     */
    @Comment("Device identifier, e.g. user agent.")
    @Column(name = "device_id", nullable = false)
    private String deviceId;

    /**
     * User to whom this refresh token belongs.
     */
    @Comment("Reference to the user owning this refresh token.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserCredential user;
}
