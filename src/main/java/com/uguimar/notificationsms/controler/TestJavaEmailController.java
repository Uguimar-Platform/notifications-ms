package com.uguimar.notificationsms.controler;

import com.uguimar.notificationsms.domain.model.Email;
import com.uguimar.notificationsms.application.port.output.EmailSender;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//Controlador REST para enviar correos de prueba
@RestController
@RequestMapping("/test-email")
public class TestJavaEmailController {
    
    @Autowired
    private EmailSender emailSender;

    //Endpoint POST para enviar correo de prueba (probado con postman y en la terminal)
    @PostMapping
    public Mono<String> sendTestEmail() {
        // Datos de prueba - Reemplazar con valores reales para testing
        Email email = new Email(
            "example@correo.com",  // correo para el destinatario deben cambiarlo por un correo suyo en caso de querer probarlo como test
            "Asunto de prueba", 
            "COD123"
        );
        return emailSender.sendEmail(email)
            .thenReturn("Correo enviado con éxito");
    }

    //Endpoint de verificación de estado del servicio
    @GetMapping("/check")
    public String check() {
        return "Servicio REST activo (puerto 8080) | gRPC en 9090";
    }
}
