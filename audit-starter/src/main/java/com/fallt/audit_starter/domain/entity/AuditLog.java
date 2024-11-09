package com.fallt.audit_starter.domain.entity;

import com.fallt.audit_starter.domain.entity.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
