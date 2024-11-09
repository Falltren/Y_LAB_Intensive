package com.fallt.security;

import com.fallt.audit_starter.security.UserDetails;
import com.fallt.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final JwtUtil jwtUtil;
    private final CommonUtil commonUtil;

    @Override
    public String getUserName() {
        String authHeader = commonUtil.getAuthHeader();
        return authHeader != null ? jwtUtil.getUserEmail(commonUtil.getAuthHeader()) : "anonymous";
    }
}
