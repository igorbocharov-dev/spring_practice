package com.practice.spring.security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.practice.spring.config.jwt.JwtTokenProperties;
import com.practice.spring.dto.security.TokenSubject;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService {

    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;
    private final JwtTokenProperties jwtTokenProperties;

    public JwtTokenService(JwtTokenProperties jwtTokenProperties) {
        this.algorithm = Algorithm.HMAC256(jwtTokenProperties.getJwtSecretKey());
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer(jwtTokenProperties.getIssuer())
                .acceptLeeway(jwtTokenProperties.getLeewaySeconds())
                .build();
        this.jwtTokenProperties = jwtTokenProperties;
    }

    // У пользователя может быть много ролей, поэтому withArrayClaim() на будущее.

    public String generateToken(TokenSubject subject){
        Instant now = Instant.now();
        Date expirationDate = Date.from(now.plus(jwtTokenProperties.getAccessTokenDuration()));
        return JWT.create()
                .withIssuer(jwtTokenProperties.getIssuer())
                .withSubject(subject.username())
                .withArrayClaim("roles", subject.authorities().toArray(String[]::new))
                .withIssuedAt(Date.from(now))
                .withExpiresAt(expirationDate)
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        return jwtVerifier.verify(token);
    }
}
