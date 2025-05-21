package com.uguimar.notificationsms.infrastructure.output.mail;

import com.uguimar.notificationsms.application.port.output.EmailSender;
import com.uguimar.notificationsms.domain.exception.EmailDeliveryException;
import com.uguimar.notificationsms.domain.model.EmailNotification;
import com.uguimar.notificationsms.domain.model.NotificationType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
@Log4j2
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    @Override
    public Mono<Boolean> send(EmailNotification notification) {
        return Mono.fromCallable(() -> {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(fromEmail);
                helper.setTo(notification.getTo());
                helper.setSubject(notification.getSubject());
                helper.setText(notification.getHtmlContent(), true);

                mailSender.send(message);
                return true;
            } catch (MessagingException e) {
                log.error("Failed to send email: {}", e.getMessage());
                throw new EmailDeliveryException("Failed to send email: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private String getTemplateName(NotificationType type) {
        switch (type) {
            case VERIFICATION_CODE -> {
                return "verification-code";
            }
            case WELCOME -> {
                return "welcome-email";
            }
            default -> {
                throw new IllegalArgumentException("Unsupported notification type: " + type);
            }
        }
    }
}
