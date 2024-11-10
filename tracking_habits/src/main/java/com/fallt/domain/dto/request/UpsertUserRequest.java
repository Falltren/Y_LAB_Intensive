package com.fallt.domain.dto.request;

import com.fallt.util.Constant;
import com.fallt.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import static com.fallt.util.Constant.MAX_EMAIL_LENGTH_MESSAGE;
import static com.fallt.util.Constant.NAME_LENGTH_MESSAGE;
import static com.fallt.util.Constant.PASSWORD_FORMAT_MESSAGE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertUserRequest {

    @NotBlank(message = NAME_LENGTH_MESSAGE)
    @Length(min = 3, max = 30, message = NAME_LENGTH_MESSAGE)
    private String name;

    @Password(message = PASSWORD_FORMAT_MESSAGE)
    private String password;

    @Length(max = 30, message = MAX_EMAIL_LENGTH_MESSAGE)
    @Email(regexp = "^[A-z0-9._%+-]+@[A-z0-9.-]+\\.[A-z]{2,6}$", message = Constant.INCORRECT_EMAIL_FORMAT_MESSAGE)
    private String email;

}
