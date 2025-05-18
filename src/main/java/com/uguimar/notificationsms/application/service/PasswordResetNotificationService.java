package com.uguimar.notificationsms.application.service;

import com.uguimar.notificationsms.application.port.input.PasswordResetNotificationUseCase;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class PasswordResetNotificationService implements PasswordResetNotificationUseCase {

    @Value("${spring.mail.username}")
    private  String userIssuing;


    private final JavaMailSender mailsender;

    public PasswordResetNotificationService(JavaMailSender mailsender) {
        this.mailsender = mailsender;
    }

    @Override
    public Mono<String> sendPasswordResetEmail(String emailReceptor, String code) {

        System.out.println("--------------------"+userIssuing);
        String htmlBody = generatePasswordResetEmailContent(emailReceptor, code);

        return Mono.fromCallable(() -> {
            try {
                MimeMessage message = mailsender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(new InternetAddress("tarrillollontopvictormanuel04@gmail.com"));
                helper.setTo(new InternetAddress(emailReceptor));
                helper.setSubject("Restablecimiento de contraseña");
                helper.setText(htmlBody, true);

                mailsender.send(message);
                log.info("Correo de restablecimiento de contraseña enviado exitosamente a: {}", emailReceptor);
                return "Correo enviado exitosamente a " + emailReceptor;
            } catch (Exception e) {
                log.error("Error al enviar correo de restablecimiento de contraseña a {}: {}", emailReceptor, e.getMessage());
                throw new RuntimeException("Error al enviar correo electrónico", e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    
    private String generatePasswordResetEmailContent(String email, String code) {

        String currentYear = String.valueOf(LocalDateTime.now().getYear());
        String formattedDate = LocalDateTime.now().plusMinutes(30)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Document</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <p>Hola mundo </p>\n" +
                "</body>\n" +
                "</html>\n";

    }
}

