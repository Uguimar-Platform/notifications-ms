package com.uguimar.notificationsms.application.port.output;

import com.uguimar.notificationsms.domain.model.EmailNotification;
import reactor.core.publisher.Mono;

public interface EmailSender {
    Mono<Boolean> send(EmailNotification notification);
}
