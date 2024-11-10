package com.fallt.aop.authentication;

import com.fallt.security.JwtUtil;
import com.fallt.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

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
    public void checkAuthentication(JoinPoint joinPoint) {
        String authHeader = commonUtil.getAuthHeader();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        HasRole hasRole = method.getAnnotation(HasRole.class);
        if (hasRole != null) {
            jwtUtil.verifyUserRole(authHeader, hasRole.mustBe());
        } else {
            jwtUtil.verifyTokenFromHeader(authHeader);
        }
    }

}
