package com.miniuber.auth.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Publisher for user registration events to Kafka
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationEventPublisher {

    private final ObjectMapper objectMapper;

    private static final String USER_REGISTERED_TOPIC = "user.registered";
    private static final String DRIVER_REGISTERED_TOPIC = "driver.registered";

    /**
     * Publish rider registration event
     */
    public void publishRiderRegistration(Long userId, String email, String name) {
        UserRegistrationEvent event = new UserRegistrationEvent(
                userId,
                email,
                name,
                "RIDER",
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        );

        // Direct method call or logic here instead of event publishing
    }

    /**
     * Publish driver registration event
     */
    public void publishDriverRegistration(Long userId, String email, String name) {
        UserRegistrationEvent event = new UserRegistrationEvent(
                userId,
                email,
                name,
                "DRIVER",
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        );

        // Direct method call or logic here instead of event publishing
    }

    /**
     * Internal method to publish event to Kafka
     */
    // Event publishing removed. Add direct logic or method calls as needed.
}
