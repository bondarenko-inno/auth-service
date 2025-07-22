package org.ebndrnk.authorizationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.ebndrnk.authorizationservice.kafka.dto.UserCreatedEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@TestConfiguration
public class KafkaTestConfig {

    @Bean
    @Primary
    public KafkaTemplate<String, UserCreatedEvent> testKafkaTemplate() {
        KafkaTemplate<String, UserCreatedEvent> kafkaTemplate = mock(KafkaTemplate.class);
        when(kafkaTemplate.send(any(), any())).thenReturn(null);
        return kafkaTemplate;
    }

    @Bean
    @Primary
    public NewTopic testUserCreatedTopic() {
        return new NewTopic("user.created", 1, (short) 1);
    }
}
