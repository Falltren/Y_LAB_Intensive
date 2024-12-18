package com.fallt.dto.response;

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
