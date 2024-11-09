package com.fallt.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fallt.exception.AuthenticationException;
import com.fallt.exception.AuthorizationException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.lifeTime}")
    private long tokenLifetime;

    @Value("${jwt.issuer}")
    private String issuer;
    private Algorithm algorithm;
    private static final String START_HEADER = "Bearer ";

    @PostConstruct
    public void init() {
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    public String generateToken(String email, String role) {
        return JWT.create()
                .withIssuer(issuer)
                .withClaim("email", email)
                .withClaim("role", role)
                .withExpiresAt(new Date(System.currentTimeMillis() + tokenLifetime))
                .sign(algorithm);
    }

    public void verifyTokenFromHeader(String authHeader) {
        if (authHeader == null) {
            throw new AuthenticationException("Необходимо аутентифицироваться");
        }
        String token = authHeader.replace(START_HEADER, "");
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            if (decodedJWT.getExpiresAt().before(new Date(System.currentTimeMillis()))) {
                throw new AuthenticationException("Время действия токена истекло");
            }
        } catch (JWTVerificationException ex) {
            throw new AuthenticationException("Токен не найден");
        }
    }

    public void verifyAdminTokenFromHeader(String authHeader) {
        verifyTokenFromHeader(authHeader);
        if (!getUserRole(authHeader).equals("ROLE_ADMIN")) {
            throw new AuthorizationException("У вас нет прав на данное действие");
        }
    }

    public String getUserEmail(String authHeader) {
        String token = authHeader.replace(START_HEADER, "");
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getClaim("email").asString();
    }

    private String getUserRole(String authHeader) {
        String token = authHeader.replace(START_HEADER, "");
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getClaim("role").asString();
    }

}
