package com.fallt.entity;

import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class User {

    private String name;

    private String email;

    @ToString.Exclude
    private String password;

    private Role role;

    private Instant createAt;

    private Instant updateAt;

    private boolean isBlocked;

    @Builder.Default
    private List<Habit> habits = new ArrayList<>();

}
