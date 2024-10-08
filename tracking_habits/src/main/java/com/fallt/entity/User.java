package com.fallt.entity;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class User {

    private Long id;

    private String name;

    private String email;

    private String password;

    private Role role;

    private Instant createAt;

    private Instant updateAt;

    private boolean isBlocked;

}
