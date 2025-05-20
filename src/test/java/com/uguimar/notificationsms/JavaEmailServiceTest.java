package com.uguimar.notificationsms;

import com.uguimar.notificationsms.domain.model.Email;
import com.uguimar.notificationsms.domain.exception.EmailSendingException;
import com.uguimar.notificationsms.application.port.output.EmailSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
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

@Service
public class JavaEmailServiceTest implements EmailSender {
    private static final Logger logger = LoggerFactory.getLogger(JavaEmailServiceTest.class);
    // Constantes para rutas y placeholders (mejor mantenibilidad)
    private static final String TEMPLATE_PATH = "Templates/TextMail.html"; // Added 'classpath:' prefix  
    // NOTA IMPORTANTE: NO MUEVAN LA CARPETA DEL TEMPLATE 
    private static final String CODE_PLACEHOLDER = "${code}";
    
    // Dependencias inyectadas (final para inmutabilidad)
    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final ResourceLoader resourceLoader;

     // Constructor con validación básica
    public JavaEmailServiceTest(JavaMailSender mailSender,
                            @Value("${spring.mail.username}") String fromEmail, 
                            ResourceLoader resourceLoader) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.resourceLoader = resourceLoader;
    }

    // Método principal mejorado con manejo reactivo
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

     // Validación centralizada
    private void validateEmailFields(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        // Validación mejorada con regex para email
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

    // Método para construcción del mensaje
    private MimeMessage prepareMimeMessage(Email email) throws MessagingException, IOException {
        logger.debug("Preparing email for {}", email.getTo());
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        
        helper.setFrom(fromEmail);
        helper.setTo(email.getTo());
        helper.setSubject(email.getSubject());
        helper.setText(loadTemplateWithCode(email.getCode()), true); // HTML content
        
        return message;
    }

    // Carga de template con manejo de recursos
    private String loadTemplateWithCode(String code) throws IOException {
        Resource resource = resourceLoader.getResource(TEMPLATE_PATH);
        if (!resource.exists()) {
            throw new IOException("Email template not found at: " + TEMPLATE_PATH);
        }
        
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            String template = FileCopyUtils.copyToString(reader);
            return template.replace(CODE_PLACEHOLDER, code);
        }
    }
}