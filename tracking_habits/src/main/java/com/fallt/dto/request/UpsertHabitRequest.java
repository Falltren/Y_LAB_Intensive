package com.fallt.dto.request;

import com.fallt.entity.ExecutionRate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertHabitRequest {

    private String title;

    private String text;

    private String rate;
}
