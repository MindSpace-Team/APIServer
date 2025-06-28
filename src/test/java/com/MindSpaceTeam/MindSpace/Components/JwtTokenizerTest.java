package com.MindSpaceTeam.MindSpace.Components;

import com.MindSpaceTeam.MindSpace.Components.Auth.Token.JwtTokenizer;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Date;


@ExtendWith(SpringExtension.class)
class JwtTokenizerTest {
    private static final String secretKey = "X9rT7eQm2WvNpLcGzYbUhKaV6sDfJxMzEpQrAtHbCjLoFiUw";
    private static final String iss = "Mindspace";
    JwtTokenizer tokenizer = new JwtTokenizer(secretKey, iss);

    @Test
    @DisplayName("Access Token생성 로직 테스트")
    void createAccessTokenTest() {
        long now = Instant.now().getEpochSecond();
        Date iat = Date.from(Instant.ofEpochSecond(now));
        Date exp = Date.from(Instant.ofEpochSecond(now + 15 * 60));
        String sub = "user1";

        String accessToken = tokenizer.createAccessToken(sub, iat);

        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decoded = verifier.verify(accessToken);

        decoded.getIssuer();
        Assertions.assertThat(decoded.getIssuer()).isEqualTo(iss);
        Assertions.assertThat(decoded.getIssuedAt()).isEqualTo(iat);
        Assertions.assertThat(decoded.getExpiresAt()).isEqualTo(exp);
        Assertions.assertThat(decoded.getSubject()).isEqualTo(sub);
    }

}