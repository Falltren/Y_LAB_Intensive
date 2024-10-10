package com.fallt.entity;

import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Habit {

    @ToString.Include
    private String title;

    @ToString.Include
    private String text;

    private ExecutionRate executionRate;

    private LocalDate createAt;

    private User user;

    @ToString.Include
    private Set<LocalDate> successfulExecution = new HashSet<>();
}
