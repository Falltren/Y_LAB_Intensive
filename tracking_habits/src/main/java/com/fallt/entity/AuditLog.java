package com.fallt.entity;

import com.fallt.aop.audit.ActionType;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    private Long id;
    private String userEmail;
    private ActionType action;
    private String description;

    @Builder.Default
    private LocalDateTime date = LocalDateTime.now();
}
