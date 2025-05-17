package com.uguimar.notificationsms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
//import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import com.uguimar.notificationsms.domain.model.Email;
import com.uguimar.notificationsms.application.service.SendGridEmailService;
import com.uguimar.notificationsms.domain.exception.EmailSendingException;

@SpringBootTest
@ActiveProfiles("prod") // Perfil Prod para pruebas con SendGrid real (revisar el archivo profiles.yml)
public class SendGridEmailServiceProd {

    private final SendGridEmailService emailService;

    public SendGridEmailServiceProd(SendGridEmailService emailService) {
        this.emailService = emailService;
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "SENDGRID_API_KEY", matches = ".+")
    public void testSendEmailSuccess() {
        // Email de prueba usando el dominio que se le permita con SendGrid
        Email testEmail = new Email("test@example.com", "Test de Prod con SendGrid", "Quiero un cafe");

        StepVerifier.create(emailService.sendEmail(testEmail))
            .verifyComplete(); 
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "SENDGRID_API_KEY", matches = ".+")
    public void testSendEmailFailure() {
        // Email malformado para probar el error se espera que se lanze un exeption
        Email testEmail = new Email("invalid-email", "Asunto inválido", "");

        StepVerifier.create(emailService.sendEmail(testEmail))
            .expectError(EmailSendingException.class)
            .verify();
    }
}