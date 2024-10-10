package com.fallt.dto;

import com.fallt.entity.ExecutionRate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HabitDto {

    private String title;

    private String text;

    private ExecutionRate rate;
}
