package com.uguimar.notificationsms.application.service;

import com.uguimar.notificationsms.application.port.input.NotificationUseCase;
import com.uguimar.notificationsms.application.port.output.EmailSender;
import com.uguimar.notificationsms.application.port.output.TemplateRepository;
import com.uguimar.notificationsms.domain.NotificationValidator;
import com.uguimar.notificationsms.domain.model.EmailNotification;
import com.uguimar.notificationsms.domain.model.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationService implements NotificationUseCase {

    private final EmailSender emailSender;
    private final TemplateRepository templateRepository;
    private final NotificationValidator validator;

    @Override
    public Mono<Boolean> sendVerificationEmail(String email, String username, String verificationCode) {
        return Mono.fromCallable(() -> {
                    validator.validateVerificationData(email, username, verificationCode);
                    return true;
                })
                .doOnSuccess(v -> log.info("Preparing verification email for user: {}", username))
                .flatMap(v -> {
                    Map<String, Object> model = Map.of(
                            "username", username,
                            "verificationCode", verificationCode
                    );

                    // Procesar la plantilla utilizando solo el tipo de notificación
                    return templateRepository.process(
                            NotificationType.VERIFICATION_CODE,
                            model
                    ).flatMap(htmlContent -> {
                        EmailNotification notification = EmailNotification.builder()
                                .to(email)
                                .subject("Verifica tu cuenta en Uguimar")
                                .htmlContent(htmlContent)
                                .build();

                        return emailSender.send(notification);
                    });
                })
                .doOnSuccess(success -> log.info("Verification email sent to {}: {}", email, success))
                .doOnError(error -> log.error("Error sending verification email to {}: {}", email, error.getMessage()));
    }

    @Override
    public Mono<Boolean> sendWelcomeEmail(String email, String username) {
        return Mono.fromCallable(() -> {
                    validator.validateWelcomeData(email, username);
                    return true;
                })
                .doOnSuccess(v -> log.info("Preparing welcome email for user: {}", username))
                .flatMap(v -> {
                    Map<String, Object> model = Map.of(
                            "username", username
                    );

                    return templateRepository.process(
                            NotificationType.WELCOME,
                            model
                    ).flatMap(htmlContent -> {
                        EmailNotification notification = EmailNotification.builder()
                                .to(email)
                                .subject("¡Bienvenido a Uguimar!")
                                .htmlContent(htmlContent)
                                .build();

                        return emailSender.send(notification);
                    });
                })
                .doOnSuccess(success -> log.info("Welcome email sent to {}: {}", email, success))
                .doOnError(error -> log.error("Error sending welcome email to {}: {}", email, error.getMessage()));
    }

    @Override
    public Mono<Boolean> sendPasswordResetEmail(String email, String username, String resetCode) {
        return Mono.fromCallable(() -> {
                    validator.validatePasswordResetData(email, username, resetCode);
                    return true;
                })
                .doOnSuccess(v -> log.info("Preparing password reset email for user: {}", username))
                .flatMap(v -> {
                    Map<String, Object> model = Map.of(
                            "username", username,
                            "resetCode", resetCode
                    );

                    return templateRepository.process(
                            NotificationType.PASSWORD_RESET,
                            model
                    ).flatMap(htmlContent -> {
                        EmailNotification notification = EmailNotification.builder()
                                .to(email)
                                .subject("Restablecimiento de contraseña en Uguimar")
                                .htmlContent(htmlContent)
                                .build();

                        return emailSender.send(notification);
                    });
                })
                .doOnSuccess(success -> log.info("Password reset email sent to {}: {}", email, success))
                .doOnError(error -> log.error("Error sending password reset email to {}: {}", email, error.getMessage()));
    }

    @Override
    public Mono<Boolean> sendPasswordResetConfirmationEmail(String email, String username) {
        return Mono.fromCallable(() -> {
                    validator.validatePasswordResetConfirmationData(email, username);
                    return true;
                })
                .doOnSuccess(v -> log.info("Preparing password reset confirmation email for user: {}", username))
                .flatMap(v -> {
                    Map<String, Object> model = Map.of(
                            "username", username,
                            "email", email
                    );

                    return templateRepository.process(
                            NotificationType.PASSWORD_RESET_CONFIRMATION,
                            model
                    ).flatMap(htmlContent -> {
                        EmailNotification notification = EmailNotification.builder()
                                .to(email)
                                .subject("Contraseña restablecida correctamente - Uguimar")
                                .htmlContent(htmlContent)
                                .build();

                        return emailSender.send(notification);
                    });
                })
                .doOnSuccess(success -> log.info("Password reset confirmation email sent to {}: {}", email, success))
                .doOnError(error -> log.error("Error sending password reset confirmation email to {}: {}", email, error.getMessage()));
    }
}