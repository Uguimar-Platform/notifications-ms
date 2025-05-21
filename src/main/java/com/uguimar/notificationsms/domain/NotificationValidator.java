package com.uguimar.notificationsms.domain;

import com.uguimar.notificationsms.domain.exception.InvalidNotificationDataException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Componente para validar datos de notificaciones
 */
@Component
public class NotificationValidator {

    // Esta expresión regular valida el formato básico de email: parte-local@dominio.tld
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    /**
     * Valida datos para correo de verificación
     */
    public void validateVerificationData(String email, String username, String verificationCode) {
        validateEmail(email);
        validateUsername(username);
        validateCode(verificationCode);
    }

    /**
     * Valida datos para correo de bienvenida
     */
    public void validateWelcomeData(String email, String username) {
        validateEmail(email);
        validateUsername(username);
    }

    /**
     * Valida datos para correo de restablecimiento de contraseña
     */
    public void validatePasswordResetData(String email, String username, String resetCode) {
        validateEmail(email);
        validateUsername(username);
        validateCode(resetCode);
    }

    /**
     * Valida datos para correo de confirmación de restablecimiento de contraseña
     */
    public void validatePasswordResetConfirmationData(String email, String username) {
        validateEmail(email);
        validateUsername(username);
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidNotificationDataException("El email no puede estar vacío");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidNotificationDataException("Formato de email inválido: " + email);
        }
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidNotificationDataException("El nombre de usuario no puede estar vacío");
        }
    }

    private void validateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new InvalidNotificationDataException("El código no puede estar vacío");
        }

        if (!code.matches("^[0-9]{6}$")) {
            throw new InvalidNotificationDataException("El código debe ser numérico de 6 dígitos");
        }
    }
}