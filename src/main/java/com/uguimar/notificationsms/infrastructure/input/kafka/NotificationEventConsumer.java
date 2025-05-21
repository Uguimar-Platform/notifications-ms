package com.uguimar.notificationsms.infrastructure.input.kafka;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uguimar.notificationsms.application.port.input.NotificationUseCase;
import com.uguimar.notificationsms.domain.exception.InvalidNotificationDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class NotificationEventConsumer {

    private final NotificationUseCase notificationUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.verification-code}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeVerificationCodeEvents(String message) {
        try {
            log.info("Received verification code event: {}", message);
            Map<String, String> event = objectMapper.readValue(message, new TypeReference<>() {
            });

            notificationUseCase.sendVerificationEmail(
                    event.get("email"),
                    event.get("username"),
                    event.get("code")
            ).subscribe(
                    success -> log.info("Processed verification email event: {}", success),
                    error -> {
                        if (error instanceof InvalidNotificationDataException) {
                            log.warn("Invalid notification data: {}", error.getMessage());
                        } else {
                            log.error("Error processing verification event", error);
                        }
                    }
            );
        } catch (Exception e) {
            log.error("Error processing verification code event: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "${kafka.topics.welcome}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeWelcomeEvents(String message) {
        try {
            log.info("Received welcome event: {}", message);
            Map<String, String> event = objectMapper.readValue(message, new TypeReference<>() {
            });

            notificationUseCase.sendWelcomeEmail(
                    event.get("email"),
                    event.get("username")
            ).subscribe(
                    success -> log.info("Processed welcome email event: {}", success),
                    error -> {
                        if (error instanceof InvalidNotificationDataException) {
                            log.warn("Invalid notification data: {}", error.getMessage());
                        } else {
                            log.error("Error processing welcome event", error);
                        }
                    }
            );
        } catch (Exception e) {
            log.error("Error processing welcome event: {}", e.getMessage());
        }
    }
}
