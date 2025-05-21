package com.uguimar.notificationsms.domain.exception;

/**
 * Excepción lanzada cuando ocurre un error al intentar enviar un correo electrónico.
 */
public class EmailDeliveryException extends RuntimeException {

    public EmailDeliveryException(String message) {
        super(message);
    }

    public EmailDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}