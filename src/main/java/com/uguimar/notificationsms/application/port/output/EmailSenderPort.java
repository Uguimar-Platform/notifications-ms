package com.uguimar.notificationsms.application.port.output;

import com.uguimar.notificationsms.domain.model.Email;
import reactor.core.publisher.Mono;

public interface EmailSenderPort {
    /**
     * Envía un correo electrónico
     * 
     * @param email Datos del correo a enviar
     * @return Estado del envío del correo
     */
    Mono<String> sendEmail(Email email);
}
