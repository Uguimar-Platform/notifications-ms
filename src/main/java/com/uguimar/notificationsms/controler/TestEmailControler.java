package com.uguimar.notificationsms.controler;
import com.uguimar.notificationsms.domain.model.Email;
import com.uguimar.notificationsms.application.port.output.EmailSender;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//Controlador REST para enviar correos de prueba
@RestController
@RequestMapping("/test-email")
public class TestEmailControler {
    
    @Autowired
    private EmailSender emailSender;  //// Servicio para enviar correos 

    @GetMapping
    public Mono<String> sendTestEmail() {
        Email email = new Email(
            "Example@correo.com", 
            "Prueba usando SendGrid", 
            "¡Este es un correo de prueba LO LOGRASTE CHAVAL!"
        );
        return emailSender.sendEmail(email)
            .thenReturn("Correo enviado con éxito");
    }

}
