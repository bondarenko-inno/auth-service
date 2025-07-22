package org.ebndrnk.authorizationservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.ebndrnk.authorizationservice.AuthorizationServiceApplication;
import org.ebndrnk.authorizationservice.config.TestContainersConfig;
import org.ebndrnk.authorizationservice.kafka.dto.UserCreatedEvent;
import org.ebndrnk.authorizationservice.model.dto.RegistrationRequest;
import org.ebndrnk.authorizationservice.repository.RefreshTokenRepository;
import org.ebndrnk.authorizationservice.repository.UserCredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AuthorizationServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegistrationKafkaPublishTest extends TestContainersConfig {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private MockMvc mockMvc;

    private KafkaConsumer<String, UserCreatedEvent> consumer;

    @BeforeEach
    void setup() {
        refreshTokenRepository.deleteAll();
        userCredentialRepository.deleteAll();

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(
                kafkaContainer.getBootstrapServers(),
                "test-consumer-group",
                "false"
        );

        Properties props = new Properties();
        props.putAll(consumerProps);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.springframework.kafka.support.serializer.JsonDeserializer");
        props.put("spring.json.value.default.type", UserCreatedEvent.class.getName());
        props.put("spring.json.trusted.packages", "*");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton("user.created"));
    }

    @Test
    void shouldPublishUserCreatedEventToKafka() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        RegistrationRequest request = new RegistrationRequest(
                "test@example.com",
                "password123",
                LocalDateTime.of(1990, 1, 1, 0, 0),
                "John",
                "Doe"
        );

        mockMvc.perform(post("/register")
                        .header("User-Agent", "JUnit-Test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        ConsumerRecords<String, UserCreatedEvent> records = consumer.poll(Duration.ofSeconds(5));
        assertThat(records.isEmpty()).isFalse();

        boolean found = false;
        for (ConsumerRecord<String, UserCreatedEvent> record : records) {
            UserCreatedEvent event = record.value();
            if (event != null && event.email().equals("test@example.com")) {
                found = true;
                break;
            }
        }

        assertThat(found)
                .as("Expected UserCreatedEvent with email 'test@example.com' to be published")
                .isTrue();
    }
}
