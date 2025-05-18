package com.uguimar.notificationsms.application.service;

import com.uguimar.notificationsms.application.port.output.EmailSenderPort;
import com.uguimar.notificationsms.domain.model.Email;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetNotificationServiceTest {

    @Mock
    private EmailSenderPort emailSenderPort;

    @InjectMocks
    private PasswordResetNotificationService service;

    @Captor
    private ArgumentCaptor<Email> emailCaptor;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_CODE = "123456";
    private static final String SUCCESS_RESPONSE = "Correo enviado exitosamente";

    @BeforeEach
    void setUp() {
        when(emailSenderPort.sendEmail(any(Email.class)))
                .thenReturn(Mono.just(SUCCESS_RESPONSE));
    }

    @Test
    void sendPasswordResetEmail_ShouldCreateAndSendEmail() throws MessagingException {
        StepVerifier.create(service.sendPasswordResetEmail(TEST_EMAIL, TEST_CODE))
                .expectNext(SUCCESS_RESPONSE)
                .verifyComplete();

        verify(emailSenderPort).sendEmail(emailCaptor.capture());
        
        Email capturedEmail = emailCaptor.getValue();
        assertEquals(TEST_EMAIL, capturedEmail.getTo());
        assertEquals("Restablecimiento de contraseña - Plataforma Uguimar", capturedEmail.getSubject());
        assertTrue(capturedEmail.getBody().contains(TEST_CODE));
        assertTrue(capturedEmail.getBody().contains("<!DOCTYPE html>"));
        assertTrue(capturedEmail.getBody().contains("<div class=\"code\">"));
    }
}
