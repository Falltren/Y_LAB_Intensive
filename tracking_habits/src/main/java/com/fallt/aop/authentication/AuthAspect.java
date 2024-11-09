package com.fallt.aop.authentication;

import com.fallt.security.JwtUtil;
import com.fallt.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthAspect {

    private final JwtUtil jwtUtil;
    private final CommonUtil commonUtil;

    @Before("execution(* com.fallt.controller..*(..)) " +
            "&& !execution(* com.fallt.controller.SecurityController.login(..)) " +
            "&& !execution(* com.fallt.controller.SecurityController.register(..)) " +
            "&& !execution(* com.fallt.controller.advice..*(..))")
    public void checkAuthentication() {
        String authHeader = commonUtil.getAuthHeader();
        jwtUtil.verifyTokenFromHeader(authHeader);
    }

}
