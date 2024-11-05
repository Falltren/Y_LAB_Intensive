package com.fallt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Habit {

    private Long id;
    private String title;
    private String text;
    private ExecutionRate executionRate;

    @Builder.Default
    private LocalDate createAt = LocalDate.now();

    @ToString.Exclude
    private User user;

    @Builder.Default
    private Set<LocalDate> successfulExecution = new TreeSet<>();
}
