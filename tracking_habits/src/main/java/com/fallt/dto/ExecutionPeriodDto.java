package com.fallt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionPeriodDto {

    private LocalDate startPeriod;

    private LocalDate endPeriod;

    private boolean executed;
}
