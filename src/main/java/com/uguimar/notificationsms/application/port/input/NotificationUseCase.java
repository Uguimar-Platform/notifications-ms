package com.uguimar.notificationsms.application.port.input;

import reactor.core.publisher.Mono;

public interface NotificationUseCase {
    Mono<Boolean> sendVerificationEmail(String email, String username, String verificationCode);

    Mono<Boolean> sendWelcomeEmail(String email, String username);

    Mono<Boolean> sendPasswordResetEmail(String email, String username, String resetCode);

    Mono<Boolean> sendPasswordResetConfirmationEmail(String email, String username);
}
