package com.uguimar.notificationsms.application.service;
//Este archivo interactua con JavaEmailTest
//Servicio Usado JavaEmail

import com.uguimar.notificationsms.application.port.output.EmailSender;
import com.uguimar.notificationsms.domain.model.Email;
import com.uguimar.notificationsms.domain.exception.EmailSendingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
    public class JavaEmailService implements EmailSender {
        private static final Logger logger = LoggerFactory.getLogger(JavaEmailService.class);
    
    private final JavaMailSender mailSender;
    private final String fromEmail;
    public JavaEmailService(JavaMailSender mailSender,
                            @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }


    @Override
    public Mono<Void> sendEmail(Email email) {
        if (email == null) {
            return Mono.error(new IllegalArgumentException("Email cannot be null"));
        }

        return Mono.fromRunnable(() -> {
            try {
                validateEmailFields(email);
                sendMimeEmail(email);
            } catch (MessagingException e) {
                handleEmailSendingError(email, e);
            }
        });
    }

    private void validateEmailFields(Email email) {
        if (email.getTo() == null || email.getTo().isBlank()) {
            throw new IllegalArgumentException("Recipient email address cannot be empty");
        }
        if (email.getSubject() == null || email.getSubject().isBlank()) {
            throw new IllegalArgumentException("Email subject cannot be empty");
        }
        if (email.getBody() == null) {
            throw new IllegalArgumentException("Email body cannot be null");
        }
    }

    private void sendMimeEmail(Email email) throws MessagingException {
        logger.debug("Preparing to send email to {}", email.getTo());
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        
        helper.setFrom(fromEmail);
        helper.setTo(email.getTo());
        helper.setSubject(email.getSubject());
        helper.setText(email.getBody(), false); // false indica texto plano
        
        mailSender.send(message);
        logger.info("Email successfully sent to {}", email.getTo());
    }

    private void handleEmailSendingError(Email email, MessagingException e) {
        String errorMessage = String.format("Failed to send email to %s. Reason: %s", 
                                          email.getTo(), e.getMessage());
        logger.error(errorMessage, e);
        throw new EmailSendingException(errorMessage, e);
    }
}