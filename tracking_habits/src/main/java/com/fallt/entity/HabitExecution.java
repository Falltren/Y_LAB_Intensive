package com.fallt.entity;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class HabitExecution {

    private Long id;

    private LocalDate date;

    private Habit habit;
}
