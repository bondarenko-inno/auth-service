package org.ebndrnk.authorizationservice;

import org.ebndrnk.authorizationservice.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AuthorizationServiceApplicationTests extends TestContainersConfig {

    @Test
    void contextLoads() {
    }

}
