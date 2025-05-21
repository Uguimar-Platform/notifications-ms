package com.uguimar.notificationsms.infrastructure.output.template;

import com.uguimar.notificationsms.application.port.output.TemplateRepository;
import com.uguimar.notificationsms.domain.exception.TemplateNotFoundException;
import com.uguimar.notificationsms.domain.model.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class ThymeleafTemplateRepository implements TemplateRepository {

    private final TemplateEngine templateEngine;

    @Override
    public Mono<String> process(NotificationType type, Map<String, Object> model) {
        String templateName = getTemplateName(type);

        return Mono.fromCallable(() -> {
            try {
                log.debug("Processing template: {} for notification type: {}", templateName, type);
                Context context = new Context();
                model.forEach(context::setVariable);

                String result = templateEngine.process(templateName, context);
                if (result == null || result.isEmpty()) {
                    throw new TemplateNotFoundException("Template not found or empty: " + templateName);
                }
                return result;
            } catch (Exception e) {
                log.error("Error processing template {}: {}", templateName, e.getMessage());
                throw new TemplateNotFoundException("Failed to process template: " + e.getMessage());
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }

    /**
     * Mapea tipos de notificación a nombres de archivos de plantilla
     * Este método centraliza la configuración de nombres de plantillas
     */
    private String getTemplateName(NotificationType type) {
        return switch (type) {
            case VERIFICATION_CODE -> "verification-email";
            case WELCOME -> "welcome-email";
            case PASSWORD_RESET -> "password-reset-email";
            case PASSWORD_RESET_CONFIRMATION -> "password-reset-confirmation-email";
        };
    }
}