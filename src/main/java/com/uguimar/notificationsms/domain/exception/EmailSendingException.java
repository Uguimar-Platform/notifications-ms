package com.uguimar.notificationsms.domain.exception;

//Excepción personalizada que se lanza cuando ocurre un error al enviar un correo
public class EmailSendingException extends RuntimeException {
    public EmailSendingException(String message) {
        super(message);
    }
    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}