package org.ebndrnk.authorizationservice.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@ContextConfiguration(initializers = TestContainersConfig.Initializer.class)
public class TestContainersConfig {

    protected static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    protected static final KafkaContainer kafkaContainer =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    static {
        postgresContainer.start();
        kafkaContainer.start();
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            String kafkaBootstrapServers = kafkaContainer.getBootstrapServers();
            String postgresUrl = postgresContainer.getJdbcUrl();

            TestPropertyValues.of(
                    "spring.datasource.url=" + postgresUrl,
                    "spring.datasource.username=" + postgresContainer.getUsername(),
                    "spring.datasource.password=" + postgresContainer.getPassword(),

                    "spring.kafka.bootstrap-servers=" + kafkaBootstrapServers,
                    "environment.kafka.address=" + kafkaBootstrapServers
            ).applyTo(context.getEnvironment());
        }
    }

}
