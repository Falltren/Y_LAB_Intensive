package com.fallt.security;

import com.fallt.domain.entity.enums.Role;
import com.fallt.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс, содержащий данные аутентифицированного пользователя
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetails {

    private String email;
    private String password;
    private Role role;

    public static UserDetails createUserDetails(User user) {
        return UserDetails.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }
}
