package com.fallt.domain.dto.request;

import com.fallt.util.Constant;
import com.fallt.validation.Password;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpsertUserRequest {

    @Length(min = 3, max = 30, message = Constant.NAME_LENGTH_MASSAGE)
    private String name;

    @Password(message = Constant.PASSWORD_FORMAT_MASSAGE)
    private String password;

    @Length(max = 30, message = Constant.MAX_EMAIL_LENGTH_MESSAGE)
    @Email(regexp = "^[A-z0-9._%+-]+@[A-z0-9.-]+\\.[A-z]{2,6}$", message = Constant.INCORRECT_EMAIL_FORMAT_MESSAGE)
    private String email;

}
