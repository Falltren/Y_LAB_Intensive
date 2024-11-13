package com.fallt.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionDto {

    private LocalDate startPeriod;
    private LocalDate endPeriod;
    private boolean executed;
}
