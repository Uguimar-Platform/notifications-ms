package com.uguimar.notificationsms.application.service;
//Este archivo interactua con SendGridEmailServices Test & IT
//Servicio Usado SendGrid 
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.uguimar.notificationsms.application.port.output.EmailSender;
import com.uguimar.notificationsms.domain.model.Email;
import com.uguimar.notificationsms.domain.exception.EmailSendingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

//Implementación del servicio EmailSender que utiliza SendGrid para enviar correos
@Service
public class SendGridEmailService implements EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);
    
    private final SendGrid sendGrid; // Cliente de SendGrid para realizar las solicitudes de envío
    private final String fromEmail; // Dirección de correo electrónico del remitente
    private final String contentType; // Tipo de contenido del correo electrónico (text/plain o text/html)

    public SendGridEmailService(SendGrid sendGrid, 
                              @Value("${sendgrid.from-email:no-reply@test.com}") String fromEmail,
                              @Value("${sendgrid.content-type:text/plain}") String contentType) {
        this.sendGrid = sendGrid;
        this.fromEmail = fromEmail;
        this.contentType = contentType;
    }

    // Envía un correo electrónico utilizando SendGrid

    @Override
    public Mono<Void> sendEmail(Email email) {
        /// Verifica si el objeto email es nulo y devuelve un error si lo es
        if (email == null) {
            return Mono.error(new IllegalArgumentException("Email cannot be null"));
        }
        // Crea un Mono a partir de una llamada que envia el correo
        return Mono.fromCallable(() -> {
            try {
                logger.debug("Preparing to send email to {}", email.getTo());
                
                // Crea los objetos necesarios para enviar el correo
                com.sendgrid.helpers.mail.objects.Email from = new com.sendgrid.helpers.mail.objects.Email(fromEmail);
                com.sendgrid.helpers.mail.objects.Email to = new com.sendgrid.helpers.mail.objects.Email(email.getTo());
                Content content = new Content(contentType, email.getBody());

                Mail mail = new Mail(from, email.getSubject(), to, content);
                
                // Configura la solicitud para enviar el correo
                Request request = new Request();
                request.setMethod(Method.POST);
                request.setEndpoint("mail/send");
                request.setBody(mail.build());

                Response response = sendGrid.api(request);
                logger.debug("SendGrid response status: {}", response.getStatusCode());
                
                // Verifica si hubo un error en la respuesta
                if (response.getStatusCode() >= 400) {
                    throw new EmailSendingException("SendGrid error: " + response.getStatusCode() + 
                                                  ", Body: " + response.getBody());
                }
                return null; // Indica que la operación se completo exitosamente
            } catch (Exception e) {
                logger.error("Error sending email to {}", email.getTo(), e);
                throw new EmailSendingException("Error sending email: " + e.getMessage(), e);
            }
        }).then(); // Completa el Mono
    }
}