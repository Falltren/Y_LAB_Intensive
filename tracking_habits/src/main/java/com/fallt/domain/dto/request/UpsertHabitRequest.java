package com.fallt.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import static com.fallt.util.Constant.HABIT_TEXT_LENGTH_MESSAGE;
import static com.fallt.util.Constant.HABIT_TITLE_LENGTH_MESSAGE;
import static com.fallt.util.Constant.NOT_BLANK_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertHabitRequest {

    @NotBlank(message = HABIT_TITLE_LENGTH_MESSAGE)
    @Length(min = 3, max = 30, message = HABIT_TITLE_LENGTH_MESSAGE)
    private String title;

    @Length(max = 100, message = HABIT_TEXT_LENGTH_MESSAGE)
    private String text;

    @NotBlank(message = NOT_BLANK_MESSAGE)
    private String rate;

}
