package com.fallt.entity;

import lombok.*;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class User {

    @ToString.Include
    private String name;

    @ToString.Include
    private String email;

    private String password;

    private Role role;

    private Instant createAt;

    private Instant updateAt;

    @ToString.Include
    private boolean isBlocked;

    private List<Habit> habits;

}
