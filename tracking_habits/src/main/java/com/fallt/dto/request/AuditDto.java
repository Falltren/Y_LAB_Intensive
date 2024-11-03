package com.fallt.dto.request;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Аудит действий пользователя
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditDto {
    private String message;
    private String action;
    private LocalDateTime timestamp;
}
