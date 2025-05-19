package com.uguimar.notificationsms.application.service;

import com.uguimar.notificationsms.application.port.input.PasswordResetNotificationUseCase;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
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
    public Mono<String> sendPasswordResetEmail(String emailReceptor,String username, String code) {
        String htmlBody = generatePasswordResetEmailContent(emailReceptor, username, code);

        return Mono.fromCallable(() -> {
            try {
                MimeMessage message = mailsender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(new InternetAddress(userIssuing));
                helper.setTo(new InternetAddress(emailReceptor));
                helper.setSubject("Tu contraseña de UGuimar");
                helper.setText(htmlBody, true);
                ClassPathResource logo = new ClassPathResource("img/logo.png");
                helper.addInline("logo-uguimar", logo, "img/png");
                mailsender.send(message);
                return "Correo enviado exitosamente a " + emailReceptor;

            } catch (Exception e) {
                throw new RuntimeException("Error al enviar correo electrónico", e);
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    private String generatePasswordResetEmailContent(String email, String username, String code) {
        String currentYear = String.valueOf(LocalDateTime.now().getYear());
        String formattedDate = LocalDateTime.now().plusMinutes(30)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));


        return """
                <!DOCTYPE html>
                                      <html lang="es">
                                        <head>
                                          <meta charset="UTF-8" />
                                          <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                                          <title>Restablecimiento de contraseña</title>
                                          <style>
                                            body {
                                              margin: 0;
                                              padding: 0;
                                              background-color: #f9fcff;
                                              font-family: "Segoe UI", sans-serif;
                                            }
                                            .email-wrapper {
                                              max-width: 600px;
                                              margin: 0 auto;
                                              background: #ffffff;
                                              border-radius: 12px;
                                              overflow: hidden;
                                              box-shadow: 0 4px 12px rgba(8, 31, 92, 0.1);
                                              border: 1px solid #e7e7e7;
                                            }
                                            .header {
                                              background-color: #ffffff;
                                              color: white;
                                              text-align: center;
                                            }
                
                                            .body {
                                              text-align: center;
                                              color: #081f5c;
                                            }
                                            .body h2 {
                                              font-size: 22px;
                                              color: #334eac;
                                            }
                                            .body p {
                                              font-size: 15px;
                                              line-height: 1.6;
                                              margin: 12px 0;
                                            }
                                            .code-box {
                                              background: #d0e3ff;
                                              border: 1px dashed #7096d1;
                                              border-radius: 6px;
                                              padding: 16px;
                                              margin: 24px auto;
                                              display: inline-block;
                                            }
                                            .code-box span {
                                              font-family: "Courier New", monospace;
                                              font-size: 28px;
                                              font-weight: bold;
                                              color: #081f5c;
                                              letter-spacing: 6px;
                                              user-select: all;
                                            }
                                            .footer {
                                              font-size: 13px;
                                              text-align: center;
                                              color: #666;
                                              padding: 20px;
                                              background: #e7f1ff;
                                            }
                                            .security-note {
                                              font-style: italic;
                                              font-size: 12px;
                                              color: #7096d1;
                                              margin-top: 16px;
                                            }
                
                                            .img-logo {
                                              border-bottom: #081e5c81 2px solid;
                                            }
                
                                            @media only screen and (max-width: 620px) {
                                              .body {
                                                padding: 24px 16px;
                                              }
                                              .code-box span {
                                                font-size: 22px;
                                              }
                                            }
                                          </style>
                                        </head>
                                        <body>
                                          <div class="email-wrapper">
                                            <div class="header">
                                              <img
                                                class="img-logo"
                                                src="cid:logo-uguimar"
                                                alt="Logo UGuimar"
                                                width="150"
                                              />
                                            </div>
                                            <div class="body">
                                              <h2>Hola, %s</h2>
                                              <p>
                                                Recibimos una solicitud para restablecer tu contraseña asociada con
                                                <strong>%s</strong>.
                                              </p>
                                              <p>Ingresa el siguiente código para completar el proceso:</p>
                                              <div class="code-box">
                                                <span>%s</span>
                                              </div>
                                              <p>
                                                Si no solicitaste este restablecimiento, ignora este correo o contacta
                                                soporte.
                                              </p>
                                              <p class="security-note">
                                                Nunca compartiremos tus credenciales ni pediremos información personal
                                                por este medio.
                                              </p>
                                            </div>
                                            <div class="footer">
                                              Este mensaje fue generado automáticamente.<br />
                                              &copy; %s UGUIMAR. Todos los derechos reservados.
                                            </div>
                                          </div>
                                        </body>
                                      </html>
                """.formatted(username, email, code, currentYear);

                    }

                }