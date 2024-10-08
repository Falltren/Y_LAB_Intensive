package com.fallt.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Habit {

    private Long id;

    private String title;

    private String text;

    private ExecutionRate executionRate;

    private User user;

    private List<LocalDate> successfulExecution;
}
