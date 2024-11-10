package com.fallt.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitProgress {

    private String title;
    private int successRate;
    private List<ExecutionDto> execution;

}
