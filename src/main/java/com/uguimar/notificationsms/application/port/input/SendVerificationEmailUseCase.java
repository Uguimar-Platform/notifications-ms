package com.uguimar.notificationsms.application.port.input;

import reactor.core.publisher.Mono;

public interface SendVerificationEmailUseCase {
    Mono<Void> send(String toEmail, String code);
}
