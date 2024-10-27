package com.fallt.aop.audit;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Аудит действий пользователя
 */
@RequiredArgsConstructor
@ToString
public class Audit {
    private final String message;
    private final String action;
    private final LocalDateTime timestamp;
}
