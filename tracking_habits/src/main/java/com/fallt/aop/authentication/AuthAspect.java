package com.fallt.aop.authentication;

import com.fallt.security.AuthenticationContext;
import com.fallt.util.SessionUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthAspect {

    @Autowired
    private AuthenticationContext authenticationContext;

    @Autowired
    private SessionUtils sessionUtils;

    @Before("execution(* com.fallt.controller..*(..)) " +
            "&& !execution(* com.fallt.controller.AuthController.login(..)) " +
            "&& !execution(* com.fallt.controller.UserController.createUser(..))")
    public void checkAuthentication() {
        String sessionId = sessionUtils.getSessionIdFromContext();
        authenticationContext.checkAuthentication(sessionId);
    }
}
