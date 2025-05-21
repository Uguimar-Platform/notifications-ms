package com.uguimar.notificationsms.domain.exception;

/**
 * Excepción lanzada cuando los datos proporcionados para una notificación son inválidos o insuficientes.
 */
public class InvalidNotificationDataException extends RuntimeException {

    public InvalidNotificationDataException(String message) {
        super(message);
    }

    public InvalidNotificationDataException(String message, Throwable cause) {
        super(message, cause);
    }
}