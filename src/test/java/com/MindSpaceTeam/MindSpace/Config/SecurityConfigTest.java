package com.MindSpaceTeam.MindSpace.Config;

import com.MindSpaceTeam.MindSpace.Controller.WorkSpaceController;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkSpaceController.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {"mindspace.jwt.secret-key=test-secret-key"})
@Import(SecurityConfig.class)
class SecurityConfigTest {
    @Autowired
    private MockMvc mockMvc;
    private static final String TEST_ISS = "test-iss";
    private static final String TEST_SUB = "test-sub";
    private static String testSecretKey = "test-secret-key";
    private static Algorithm algorithm = Algorithm.HMAC256(testSecretKey);

    @Test
    @DisplayName("토큰 존재하지 않는 경우 Filter Test")
    public void notExistTokenTest() throws Exception {
        mockMvc.perform(get("/workspaces"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").value("need_login"));
    }

    @Test
    @DisplayName("만료된 토큰 Filter Test")
    public void expiredTokenTest() throws Exception {
        Instant now = Instant.now();
        Date iss = Date.from(now.minusSeconds(15 * 60));
        Date exp = Date.from(now.minusSeconds(11));
        String expiredToken = JWT.create()
                .withIssuer(TEST_ISS)
                .withIssuedAt(iss)
                .withExpiresAt(exp)
                .withSubject(TEST_SUB)
                .sign(algorithm);

        mockMvc.perform(get("/workspaces")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").value("expired_token"));
    }

    @Test
    @DisplayName("무효한 토큰 Filter Test")
    public void invalidTokenTest() throws Exception {
        Algorithm invalidAlgorithm = Algorithm.HMAC256("manipulated-secret-key");
        Instant now = Instant.now();
        Date iss = Date.from(now);
        Date exp = Date.from(now.plusSeconds(15 * 60));
        String invalidToken = JWT.create()
                .withIssuer(TEST_ISS)
                .withIssuedAt(iss)
                .withExpiresAt(exp)
                .withSubject(TEST_SUB)
                .sign(invalidAlgorithm);

        mockMvc.perform(get("/workspaces")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error").value("invalid_token"));
    }

    @Test
    @DisplayName("유효한 토큰 Filter Test")
    public void validTokenTest() throws Exception {
        Instant now = Instant.now();
        Date iat = Date.from(now);
        Date exp = Date.from(now.plusSeconds(15 * 60));
        String accessToken = JWT.create()
                .withIssuer(TEST_ISS)
                .withIssuedAt(iat)
                .withExpiresAt(exp)
                .withSubject(TEST_SUB)
                .sign(algorithm);

        mockMvc.perform(get("/workspaces")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }
}