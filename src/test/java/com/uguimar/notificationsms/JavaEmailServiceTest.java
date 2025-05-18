package com.uguimar.notificationsms;

import com.uguimar.notificationsms.domain.model.Email;
import com.uguimar.notificationsms.domain.exception.EmailSendingException;
import com.uguimar.notificationsms.application.port.output.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import jakarta.mail.MessagingException;

@Service
public class JavaEmailServiceTest implements EmailSender {
    private static final Logger logger = LoggerFactory.getLogger(JavaEmailServiceTest.class);
    
    private final JavaMailSender mailSender;
    private final String fromEmail;

    public JavaEmailServiceTest(JavaMailSender mailSender,
                          @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    @Override
    public Mono<Void> sendEmail(Email email) {
        return Mono.fromCallable(() -> {
                validateEmailFields(email);
                return prepareMimeMessage(email);
            })
            .doOnSuccess(message -> {
                mailSender.send(message);
                logger.info("Email successfully sent to {}", email.getTo());
            })
            .doOnError(e -> {
                String errorMsg = "Failed to send email to " + email.getTo();
                logger.error(errorMsg, e);
                throw new EmailSendingException(errorMsg, e);
            })
            .then();
    }

    private void validateEmailFields(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
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

    private jakarta.mail.internet.MimeMessage prepareMimeMessage(Email email) 
        throws MessagingException {
        logger.debug("Preparing email for {}", email.getTo());
        
        jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        
        helper.setFrom(fromEmail);
        helper.setTo(email.getTo());
        helper.setSubject(email.getSubject());
        helper.setText(email.getBody(), false);
        
        return message;
    }
}