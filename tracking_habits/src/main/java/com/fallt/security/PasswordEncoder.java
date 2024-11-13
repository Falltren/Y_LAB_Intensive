package com.fallt.security;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    public String encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String rowPassword, String encodedPassword) {
        return BCrypt.checkpw(rowPassword, encodedPassword);
    }
}
