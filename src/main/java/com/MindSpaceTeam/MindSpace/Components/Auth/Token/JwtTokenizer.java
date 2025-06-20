package com.MindSpaceTeam.MindSpace.Components.Auth.Token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenizer {
    @Value("spring.application.name")
    private String iss;
    @Value("mindspace.jwt.secret-key")
    private String secretKey;
    private Date iat;

    public JwtTokenizer() {}

    public JwtTokenizer(String secretKey, String iss) {
        this.secretKey = secretKey;
        this.iss = iss;
    }

    public String createAccessToken(String sub, Date iat) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        Date exp = Date.from(iat.toInstant().plusSeconds(15 * 60));

        return JWT.create()
                .withIssuer(iss)
                .withIssuedAt(iat)
                .withExpiresAt(exp)
                .withSubject(sub)
                .sign(algorithm);
    }

}
