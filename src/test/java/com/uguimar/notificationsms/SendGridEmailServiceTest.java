package com.uguimar.notificationsms;

import com.uguimar.notificationsms.application.port.output.EmailSender;
import com.uguimar.notificationsms.application.service.SendGridEmailService;
import com.uguimar.notificationsms.domain.model.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Service
@Profile("test") // Solo se activa en el perfil de test (revisen el archivo profiles.yml)
public class SendGridEmailServiceTest implements EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(SendGridEmailService.class);
    
    // Guarda los emails a enviar para poder verificarlos en tests
    private final List<Email> sentEmails = new ArrayList<>();

    @Override
    public Mono<Void> sendEmail(Email email) {
        return Mono.fromRunnable(() -> {
            logger.info("[MOCK] Simulando envío de email a: {}", email.getTo());
            sentEmails.add(email); // Almacena el email para su posterior verificación
        });
    }

    // Verificación de que correos se enviaron antes
    public List<Email> getSentEmails() {
        return new ArrayList<>(sentEmails);
    }

    // Limpieza entre los emails registrados 
    public void clearSentEmails() {
        sentEmails.clear();
    }
}