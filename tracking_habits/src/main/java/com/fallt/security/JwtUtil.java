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

    public String generateToken(Long id, String email, String role) {
        return JWT.create()
                .withIssuer(issuer)
                .withClaim("id", id)
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
            verifier.verify(token);
        } catch (JWTVerificationException ex) {
            throw new AuthenticationException(ex.getMessage());
        }
    }

    public void verifyAdminTokenFromHeader(String authHeader) {
        verifyTokenFromHeader(authHeader);
        if (!getUserRole(authHeader).equals("ROLE_ADMIN")) {
            throw new AuthorizationException("У вас нет прав на данное действие");
        }
    }

    public String getUserEmail(String authHeader) {
        return getDecodedJwt(authHeader).getClaim("email").asString();
    }

    public Long getUserId(String authHeader) {
        return getDecodedJwt(authHeader).getClaim("id").asLong();
    }

    public String getUserRole(String authHeader) {
        return getDecodedJwt(authHeader).getClaim("role").asString();
    }

    private DecodedJWT getDecodedJwt(String authHeader) {
        String token = authHeader.replace(START_HEADER, "");
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        return verifier.verify(token);
    }

}
