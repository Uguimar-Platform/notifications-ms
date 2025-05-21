package com.uguimar.notificationsms.application.port.output;

import com.uguimar.notificationsms.domain.model.NotificationType;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface TemplateRepository {
    /**
     * Procesa una plantilla según el tipo de notificación y los datos del modelo
     */
    Mono<String> process(NotificationType type, Map<String, Object> model);
}
