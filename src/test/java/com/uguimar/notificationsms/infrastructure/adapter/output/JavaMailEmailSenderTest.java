package com.uguimar.notificationsms.infrastructure.adapter.output;

import com.uguimar.notificationsms.domain.model.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import reactor.test.StepVerifier;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JavaMailEmailSenderTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private JavaMailEmailSender emailSender;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    private Email testEmail;

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        testEmail = Email.builder()
                .to("test@example.com")
                .subject("Test Subject")
                .body("<p>Test Body</p>")
                .build();
    }

    @Test
    void sendEmail_Success() {
        doNothing().when(mailSender).send(any(MimeMessage.class));

        StepVerifier.create(emailSender.sendEmail(testEmail))
                .expectNext("Correo enviado exitosamente")
                .verifyComplete();

        verify(mailSender).send(mimeMessageCaptor.capture());
        assertEquals(mimeMessage, mimeMessageCaptor.getValue());
    }

    @Test
    void sendEmail_Exception() {
        doThrow(new RuntimeException("Error al enviar correo")).when(mailSender).send(any(MimeMessage.class));

        StepVerifier.create(emailSender.sendEmail(testEmail))
                .expectErrorMatches(throwable -> 
                        throwable instanceof RuntimeException && 
                        throwable.getMessage().equals("Error al enviar correo electrónico"))
                .verify();

        verify(mailSender).send(any(MimeMessage.class));
    }
}
