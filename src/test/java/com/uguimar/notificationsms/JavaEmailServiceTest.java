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
        if (email.getCode() == null) {
            throw new IllegalArgumentException("Email code cannot be null");
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
        helper.setText(buildHtmlContent(email.getCode()), true);
        
        return message;
    }

    private String buildHtmlContent(String code) {
        return """
        <html>
            <body style="font-family: 'Segoe UI', sans-serif; background-color: #f7f9fb; padding: 30px;">
                <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 40px; border-radius: 10px; box-shadow: 0px 0px 10px rgba(0,0,0,0.05);">
                    <h2 style="color: #1a73e8;">¡Tu código de verificación ha llegado!</h2>
                    <p style="font-size: 16px; color: #333;">Hola,</p>
                    <p style="font-size: 16px; color: #333;">Hemos recibido una solicitud para verificar tu identidad. Utiliza el siguiente código:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <span style="display: inline-block; font-size: 30px; font-weight: bold; color: #1a73e8; background-color: #e8f0fe; padding: 15px 30px; border-radius: 8px; letter-spacing: 3px;">
                            %s
                        </span>
                    </div>

                    <p style="font-size: 14px; color: #666;">Este código expirará en los próximos minutos. Si no solicitaste este código, por favor ignora este mensaje.</p>

                    <hr style="margin: 40px 0; border: none; border-top: 1px solid #ddd;">

                    <p style="font-size: 14px; color: #999;">Este correo fue enviado automáticamente. No respondas a este mensaje.</p>
                    <p style="font-size: 14px; color: #999;">© 2025 Uguimar Platform</p>
                </div>
            </body>
        </html>
    """.formatted(code);
    }
}