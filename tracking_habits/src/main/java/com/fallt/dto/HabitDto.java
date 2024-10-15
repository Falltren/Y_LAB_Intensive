package com.fallt.dto;

import com.fallt.entity.ExecutionRate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HabitDto {

    private String title;

    private String text;

    private ExecutionRate rate;
}
