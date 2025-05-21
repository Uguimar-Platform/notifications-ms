package com.uguimar.notificationsms.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailNotification {
    private String to;
    private String subject;
    private String htmlContent; // Contenido HTML ya procesado
}
