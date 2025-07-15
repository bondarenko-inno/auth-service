package org.ebndrnk.authorizationservice.model.entity.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.ebndrnk.authorizationservice.model.entity.BasicEntity;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "user_credentials")
@Getter
@Setter
public class UserCredential extends BasicEntity {
    @Comment("Email пользователя, служит логином. Уникален.")
    @Column(name = "email", length = 50, unique = true, nullable = false)
    @NotNull
    @Size(min = 4, max = 50)
    @Email
    private String email;

    @Comment("Пароль пользователя в захешированном виде.")
    @Column(name = "password_hash", nullable = false)
    @NotNull
    @Size(min = 10, max = 255)
    private String passwordHash;

    @Comment("Роль пользователя, например ROLE_USER, ROLE_ADMIN и т.д.")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 30, nullable = false)
    @NotNull
    private UserRole role;
}
