package com.uguimar.notificationsms.application.service;

import org.springframework.stereotype.Service;

import com.uguimar.notificationsms.application.port.input.SendVerificationEmailUseCase;
import com.uguimar.notificationsms.application.port.output.EmailSender;
import com.uguimar.notificationsms.domain.model.Email;
import reactor.core.publisher.Mono;

@Service //Para el gRPC service
public class SendVerificationEmailService implements SendVerificationEmailUseCase {
    private final EmailSender emailSender;

    public SendVerificationEmailService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
        public Mono<Void> send(String toEmail, String code) {
        String subject = "Código de verificación";
        String body = "Tu código de verificación es: " + code;
        Email email = new Email(toEmail, subject, body);
        return emailSender.sendEmail(email);
    }
}
