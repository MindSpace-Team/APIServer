package com.MindSpaceTeam.MindSpace.Service;

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
class OauthJwtServiceTest {

    @Autowired
    private OauthJwtService oauthJwtService;
    @Value("${oauth2.google.jwtTestToken}")
    private String jwtToken;
    @Value("${oauth2.google.publicKeys}")
    private String googlePublicKeys;
    @Mock
    private RestTemplate restTemplate;

    @DisplayName("JWT Token 검증 로직 Test")
    @Test
    public void jwtTokenTest() {
        Mockito.when(restTemplate.getForObject("https://www.googleapis.com/oauth2/v3/certs", String.class))
                .thenReturn(googlePublicKeys);

        boolean result = this.oauthJwtService.verifyToken(jwtToken);

        Assertions.assertTrue(result);
    }

}