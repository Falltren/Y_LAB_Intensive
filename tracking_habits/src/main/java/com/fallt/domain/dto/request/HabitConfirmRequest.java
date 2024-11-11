package com.fallt.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.fallt.util.Constant.NOT_NULL_MESSAGE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitConfirmRequest {

    private Long habitId;

    @PastOrPresent
    @NotNull(message = NOT_NULL_MESSAGE)
    private LocalDate date;

}
