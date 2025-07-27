package com.example.rabbitmq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.rabbitmq.host=localhost",
    "spring.rabbitmq.port=5672", 
    "spring.rabbitmq.username=guest",
    "spring.rabbitmq.password=guest"
})
class ApplicationContextTest {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
        // All beans should be created without errors
    }
}