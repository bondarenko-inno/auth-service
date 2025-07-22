package org.ebndrnk.authorizationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for providing a {@link PasswordEncoder} bean.
 * <p>
 * This encoder uses the BCrypt hashing algorithm and is used
 * for securely hashing user passwords.
 */
@Configuration
public class BCryptConfig {

    /**
     * Creates a {@link PasswordEncoder} bean using {@link BCryptPasswordEncoder}.
     *
     * @return a password encoder that uses BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
