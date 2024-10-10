package com.fallt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitProgress {

    private String title;

    private int successRate;

    private List<ExecutionDto> execution;
}
