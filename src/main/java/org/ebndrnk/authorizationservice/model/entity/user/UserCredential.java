package org.ebndrnk.authorizationservice.model.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.ebndrnk.authorizationservice.model.entity.BasicEntity;
import org.ebndrnk.authorizationservice.model.entity.user.token.RefreshToken;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing user credentials.
 * <p>
 * Stores login email, hashed password, user role, and related refresh tokens.
 */
@Entity
@Table(name = "user_credentials")
@Getter
@Setter
public class UserCredential extends BasicEntity {

    /**
     * The user's email address, used as a unique login identifier.
     */
    @Comment("User email address, serves as login. Unique.")
    @Column(name = "email", length = 50, unique = true, nullable = false)
    @NotNull
    @Size(min = 4, max = 50)
    @Email
    private String email;

    /**
     * Hashed password of the user.
     */
    @Comment("User password in hashed format.")
    @Column(name = "password_hash", nullable = false)
    @NotNull
    @Size(min = 10, max = 255)
    private String passwordHash;

    /**
     * Role assigned to the user, e.g. ROLE_USER, ROLE_ADMIN, etc.
     */
    @Comment("User role, e.g. ROLE_USER, ROLE_ADMIN.")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 30, nullable = false)
    @NotNull
    private UserRole role;

    /**
     * Refresh tokens associated with the user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();
}
