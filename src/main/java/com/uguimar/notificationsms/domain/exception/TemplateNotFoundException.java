package com.uguimar.notificationsms.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra una plantilla o hay un error al procesarla.
 */
public class TemplateNotFoundException extends RuntimeException {

    public TemplateNotFoundException(String message) {
        super(message);
    }

    public TemplateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}