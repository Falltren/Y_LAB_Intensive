package com.fallt.domain.dto.request;

import com.fallt.util.Constant;
import com.fallt.validation.Password;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fallt.util.Constant.PASSWORD_FORMAT_MESSAGE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @Email(regexp = "^[A-z0-9._%+-]+@[A-z0-9.-]+\\.[A-z]{2,6}$", message = Constant.INCORRECT_EMAIL_FORMAT_MESSAGE)
    private String email;

    @Password(message = PASSWORD_FORMAT_MESSAGE)
    private String password;

}
