package com.uguimar.notificationsms.application.port.input;

import jakarta.mail.MessagingException;
import reactor.core.publisher.Mono;

public interface PasswordResetNotificationUseCase  {

    Mono<String> sendPasswordResetEmail(String email, String code) ;
}
