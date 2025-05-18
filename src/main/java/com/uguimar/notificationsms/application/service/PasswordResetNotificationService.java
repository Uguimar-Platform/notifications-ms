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
    private String userIssuing;

    private final JavaMailSender mailsender;

    public PasswordResetNotificationService(JavaMailSender mailsender) {
        this.mailsender = mailsender;
    }

    @Override
    public Mono<String> sendPasswordResetEmail(String emailReceptor, String code) {
        log.info("Enviando correo de restablecimiento de contraseña a: {}", emailReceptor);
        String htmlBody = generatePasswordResetEmailContent(emailReceptor, code);

        return Mono.fromCallable(() -> {
            try {
                MimeMessage message = mailsender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(new InternetAddress(userIssuing != null ? userIssuing : "noreply@uguimar.com"));
                helper.setTo(new InternetAddress(emailReceptor));
                helper.setSubject("Restablecimiento de contraseña - UGUIMAR");
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
                "<html lang=\"es\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Restablecimiento de contraseña</title>\n" +
                "    <style>\n" +
                "        /* Estilos para todos los clientes de correo */\n" +
                "        @media only screen and (max-width: 620px) {\n" +
                "            .email-container {\n" +
                "                width: 100% !important;\n" +
                "                padding: 10px !important;\n" +
                "            }\n" +
                "            .code-container {\n" +
                "                width: 90% !important;\n" +
                "                padding: 12px 8px !important;\n" +
                "            }\n" +
                "            .content {\n" +
                "                padding: 20px 15px !important;\n" +
                "            }\n" +
                "            h1 {\n" +
                "                font-size: 20px !important;\n" +
                "            }\n" +
                "            p {\n" +
                "                font-size: 14px !important;\n" +
                "            }\n" +
                "        }\n" +
                "        /* Estilos base */\n" +
                "        body {\n" +
                "            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #f9f9f9;\n" +
                "            color: #333333;\n" +
                "            line-height: 1.5;\n" +
                "        }\n" +
                "        .email-container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 8px;\n" +
                "            overflow: hidden;\n" +
                "            box-shadow: 0 1px 3px rgba(0,0,0,0.1);\n" +
                "        }\n" +
                "        .header {\n" +
                "            background-color: #1a5276;\n" +
                "            color: #ffffff;\n" +
                "            padding: 25px 20px;\n" +
                "            text-align: center;\n" +
                "            border-bottom: 1px solid #0f3b55;\n" +
                "        }\n" +
                "        .header-title {\n" +
                "            margin: 0;\n" +
                "            font-size: 24px;\n" +
                "            font-weight: 700;\n" +
                "            text-transform: uppercase;\n" +
                "            letter-spacing: 1px;\n" +
                "        }\n" +
                "        .content {\n" +
                "            padding: 35px 25px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        h1 {\n" +
                "            color: #1a5276;\n" +
                "            margin-top: 0;\n" +
                "            margin-bottom: 20px;\n" +
                "            font-weight: 600;\n" +
                "        }\n" +
                "        p {\n" +
                "            margin: 0 0 20px;\n" +
                "            font-size: 16px;\n" +
                "            color: #555555;\n" +
                "        }\n" +
                "        .highlight {\n" +
                "            color: #1a5276;\n" +
                "            font-weight: 600;\n" +
                "        }\n" +
                "        .code-container {\n" +
                "            background-color: #f5f8fa;\n" +
                "            border: 1px solid #e1e4e8;\n" +
                "            border-radius: 6px;\n" +
                "            padding: 20px;\n" +
                "            margin: 25px auto;\n" +
                "            width: 70%;\n" +
                "            text-align: center;\n" +
                "            box-shadow: 0 2px 4px rgba(0,0,0,0.05);\n" +
                "        }\n" +
                "        .code {\n" +
                "            font-size: 32px;\n" +
                "            font-weight: 700;\n" +
                "            color: #1a5276;\n" +
                "            letter-spacing: 6px;\n" +
                "            font-family: 'Courier New', monospace;\n" +
                "            user-select: all;\n" +
                "        }\n" +
                "        .expiration {\n" +
                "            margin-top: 20px;\n" +
                "            color: #e67e22;\n" +
                "            font-size: 14px;\n" +
                "            font-weight: 500;\n" +
                "            padding: 8px 12px;\n" +
                "            background-color: #fff9e6;\n" +
                "            border-radius: 4px;\n" +
                "            display: inline-block;\n" +
                "        }\n" +
                "        .divider {\n" +
                "            height: 1px;\n" +
                "            background-color: #e1e4e8;\n" +
                "            margin: 25px 0;\n" +
                "        }\n" +
                "        .footer {\n" +
                "            background-color: #f5f8fa;\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "            font-size: 13px;\n" +
                "            color: #6c757d;\n" +
                "            border-top: 1px solid #e1e4e8;\n" +
                "        }\n" +
                "        .security-notice {\n" +
                "            font-style: italic;\n" +
                "            margin-top: 15px;\n" +
                "            font-size: 12px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"email-container\">\n" +
                "        <div class=\"header\">\n" +
                "            <div class=\"header-title\">UGUIMAR</div>\n" +
                "        </div>\n" +
                "        <div class=\"content\">\n" +
                "            <h1>Restablecimiento de Contraseña</h1>\n" +
                "            <p>Hemos recibido una solicitud para restablecer la contraseña de la cuenta asociada con <span class=\"highlight\">" + email + "</span>.</p>\n" +
                "            <p>Utiliza el siguiente código para completar el proceso:</p>\n" +
                "            <div class=\"code-container\">\n" +
                "                <div class=\"code\">" + code + "</div>\n" +
                "            </div>\n" +
                "            <div class=\"expiration\">Este código expirará el " + formattedDate + "</div>\n" +
                "            <div class=\"divider\"></div>\n" +
                "            <p>Si no has solicitado restablecer tu contraseña, puedes ignorar este correo electrónico o contactar a nuestro soporte si crees que tu cuenta podría estar comprometida.</p>\n" +
                "            <p class=\"security-notice\">Por razones de seguridad, nunca compartimos tus credenciales ni te pedimos información personal por correo electrónico.</p>\n" +
                "        </div>\n" +
                "        <div class=\"footer\">\n" +
                "            <p>Este es un correo electrónico automático. Por favor, no responda a este mensaje.</p>\n" +
                "            <p>&copy; " + currentYear + " UGUIMAR. Todos los derechos reservados.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}