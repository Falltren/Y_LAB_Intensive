package com.fallt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class User {

    private Long id;
    private String name;
    private String email;

    @ToString.Exclude
    private String password;
    private Role role;

    @Builder.Default
    private LocalDateTime createAt = LocalDateTime.now();
    private LocalDateTime updateAt;
    private boolean isBlocked;

    @Builder.Default
    private List<Habit> habits = new ArrayList<>();

}
