package com.uguimar.notificationsms.application.port.input;

import reactor.core.publisher.Mono;

public interface PasswordResetNotificationUseCase  {

    Mono<String> sendPasswordResetEmail(String email, String username, String code) ;
}
