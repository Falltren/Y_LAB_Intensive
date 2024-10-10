package com.fallt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionDailyDto {

    private LocalDate date;

    private boolean executed;
}
