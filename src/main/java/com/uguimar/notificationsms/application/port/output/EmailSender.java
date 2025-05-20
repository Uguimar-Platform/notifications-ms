package com.uguimar.notificationsms.application.port.output;

import com.uguimar.notificationsms.domain.model.Email;
import reactor.core.publisher.Mono;

public interface EmailSender {
    Mono<Void> sendEmail(Email email);
}
