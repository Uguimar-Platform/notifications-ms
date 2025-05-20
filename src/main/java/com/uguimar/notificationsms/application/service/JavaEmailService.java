package com.uguimar.notificationsms.application.service;
//Este archivo interactua con JavaEmailTest
//Servicio Usado JavaEmail

import com.uguimar.notificationsms.application.port.output.EmailSender;
import com.uguimar.notificationsms.domain.model.Email;
import com.uguimar.notificationsms.domain.exception.EmailSendingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import reactor.core.publisher.Mono;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

//Servicio para el envío de correos electrónicos utilizando JavaMailSender
//Implementa la interfaz EmailSender para proporcionar funcionalidad de envío de emails
@Service
    public class JavaEmailService implements EmailSender {
        private static final Logger logger = LoggerFactory.getLogger(JavaEmailService.class);
    
    // Componente para el envío de emails proporcionado por Spring
    private final JavaMailSender mailSender;
    // Email del remitente configurado en application.properties
    private final String fromEmail;
    // Plantilla HTML del email cargada al iniciar el servicio
    private final String emailTemplate;


    //Constructor del servicio
    public JavaEmailService(JavaMailSender mailSender,
                            @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        try {
            // Carga la plantilla HTML al iniciar el servicio
            this.emailTemplate = loadEmailTemplate();
            logger.info("Email template loaded successfully");
        } catch (IOException e) {
            String errorMsg = "Failed to load email template";
            logger.error(errorMsg, e);
            throw new EmailSendingException(errorMsg, e);
        }
    }

    //Envía un email de forma reactiva.
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

    //Valida los campos obligatorios del email.
    private void validateEmailFields(Email email) {
        if (email.getTo() == null || email.getTo().isBlank()) {
            throw new IllegalArgumentException("Recipient email address cannot be empty");
        }
        if (email.getSubject() == null || email.getSubject().isBlank()) {
            throw new IllegalArgumentException("Email subject cannot be empty");
        }
        if (email.getCode() == null) {
            throw new IllegalArgumentException("Email code cannot be null");
        }
    }

    //Construye y envía el email MIME.
    private void sendMimeEmail(Email email) throws MessagingException {
        logger.debug("Preparing to send email to {}", email.getTo());
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        
        helper.setFrom(fromEmail);
        helper.setTo(email.getTo());
        helper.setSubject(email.getSubject());
        helper.setText(buildEmailContent(email.getCode()), true); // false indica texto plano //true indica HTML
        
        mailSender.send(message);
        logger.info("Email successfully sent to {}", email.getTo());
    }

    //Maneja errores durante el envío del email.
    private void handleEmailSendingError(Email email, MessagingException e) {
        String errorMessage = String.format("Failed to send email to %s. Reason: %s", 
                                          email.getTo(), e.getMessage());
        logger.error(errorMessage, e);
        throw new EmailSendingException(errorMessage, e);
    }

    //Carga la plantilla HTML desde los recursos.
    private String loadEmailTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("Templates/TextMail.html");
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    //Reemplaza el placeholder en la plantilla con el codigo de verificacion
    private String buildEmailContent(String code) {
        return emailTemplate.replace("${code}", code);
    }
}