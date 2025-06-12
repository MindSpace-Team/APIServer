package com.MindSpaceTeam.MindSpace.Component.JWT.Verifier;

import com.MindSpaceTeam.MindSpace.Components.JWT.Verifier.GoogleJwtVerifier;
import com.MindSpaceTeam.MindSpace.dto.JWTToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class GoogleJwtVerifierTest {
    @Mock
    private RestTemplate restTemplate;
    @Value("${oauth2.google.jwtTestToken}")
    private String jwtToken;
    @Value("${oauth2.google.publicKeys}")
    private String googlePublicKeys;
    @Autowired
    private GoogleJwtVerifier googleJwtVerifier;

    @DisplayName("JWT Token 검증 로직 Test")
    @Test
    public void jwtTokenTest() {
        Mockito.when(restTemplate.getForObject("https://www.googleapis.com/oauth2/v3/certs", String.class))
                .thenReturn(googlePublicKeys);

        boolean result = this.googleJwtVerifier.verify(new JWTToken(jwtToken));

        Assertions.assertTrue(result);
    }
}