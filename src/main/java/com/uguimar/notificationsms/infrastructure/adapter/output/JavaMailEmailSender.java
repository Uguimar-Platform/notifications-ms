package com.uguimar.notificationsms.infrastructure.adapter.output;

import com.uguimar.notificationsms.application.port.output.EmailSenderPort;
import com.uguimar.notificationsms.domain.model.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class JavaMailEmailSender implements EmailSenderPort {

    private final JavaMailSender mailSender;

    @Override
    public Mono<String> sendEmail(Email email) {
        return Mono.fromCallable(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                
                helper.setTo(email.getTo());
                helper.setSubject(email.getSubject());
                helper.setText(email.getBody(), true); // true indica que es HTML
                
                mailSender.send(message);
                log.info("Correo de restablecimiento de contraseña enviado exitosamente a: {}", email.getTo());
                return "Correo enviado exitosamente";
            } catch (MessagingException e) {
                log.error("Error al enviar correo de restablecimiento de contraseña a {}: {}", email.getTo(), e.getMessage());
                throw new RuntimeException("Error al enviar correo electrónico"+ e.getMessage(), e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
