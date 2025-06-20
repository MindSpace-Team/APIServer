package com.MindSpaceTeam.MindSpace.Component.JWT.Verifier;

import com.MindSpaceTeam.MindSpace.Components.Auth.Oauth.API.GoogleOauthAPI;
import com.MindSpaceTeam.MindSpace.Components.Auth.Oauth.Verifier.GoogleJwtVerifier;
import com.MindSpaceTeam.MindSpace.Components.JsonMapper;
import com.MindSpaceTeam.MindSpace.Components.Properties.GoogleOauthProperties;
import com.MindSpaceTeam.MindSpace.dto.JWTToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(SpringExtension.class)
class GoogleJwtVerifierTest {
    private final String jwtToken = "eyJhbGciOiAiUlMyNTYiLCAidHlwIjogIkpXVCIsICJraWQiOiAidGVzdC1rZXktaWQifQ.eyJzdWIiOiAiMTIzNDU2Nzg5MCIsICJuYW1lIjogIkpvaG4gRG9lIiwgImVtYWlsIjogImpvaG5AZXhhbXBsZS5jb20ifQ.cm3MWSWjUnuz9oMqktnlg5xf0mg0bX95dTfLlK5E6TxssebEDx7jtnHc98hzEZ8QYBTBOfNBThjK2tyzXDeJ93rnVuLobcjmgIMGcp_wHi8Vfo2hrhFM7UguwHxde3LTqP8mabeIDbEkZBwUQFvEBV7teuUdIShNW8IJ4XERxTEtQQGL0xFjsUjZzFLmq3FhFIcqW4ac1u4XsHlTaejL0bZObbZFrYSpTS0fKBEqgLJwLmjX3sbSWqaqhxCgmY-eiqE7gncN4IQvK5sguhotjLM7ImpUuLHXZpTaxbeJ4KPa74bY0c0F1E6UIA5Kb67_gwY30DaoA65YeBBnx4bFKg";

    private final String googlePublicKeys = """
    {
      "keys": [
        {
          "kid": "test-key-id",
          "kty": "RSA",
          "alg": "RS256",
          "use": "sig",
          "n": "9FACiO8K0kaVnjGxKOXwxlJ-q1dQPxFn0cX4dqfDuYRG3Ub_CnbDsxIcemRoAP5iasULj8_60RQTVoWQFyPfbEk85qwr2Jnkl0EyPDdtgiskypyPlMssHKkz1DQC3dim7WaajdEDL9FelwsXMJwFMPucXtC6Ga7gJMX1Aj-5k9Zskr27Ddbr5luit0DU0xzAdFInjBkanOAgB-uaSaCuS1BJGC2pQ1kTFRdg_ODx97OjEddrYimebWLLWWGL-pS-Ftu32OEeAzqxEzdm0lNwcCx9DZ-1lLuwLFKJCL1F9CLJrlqgfG9aoLfrShBbeKhvu0vvcL4Yoy8CW9GY5klmPQ",
          "e": "AQAB"
        }
      ]
    }
    """;


    @Mock
    private RestTemplate restTemplate;
    private JsonMapper jsonMapper;
    private GoogleOauthAPI googleOauthAPI;
    private GoogleJwtVerifier googleJwtVerifier;
    private GoogleOauthProperties googleOauthProperties;

    @BeforeEach
    void setUp() {
        jsonMapper = new JsonMapper(new ObjectMapper());
        googleOauthProperties = new GoogleOauthProperties();
        googleOauthProperties.setClientId("test-client-id");
        googleOauthProperties.setClientSecret("test-client-secret");
        googleOauthProperties.setRedirectUrl("test-redirect-url");
        googleOauthProperties.setScope("test-scope");
        googleOauthProperties.setPublicKeyUrl("https://www.googleapis.com/oauth2/v3/certs");
        googleOauthAPI = new GoogleOauthAPI(jsonMapper, restTemplate, googleOauthProperties);
        googleJwtVerifier = new GoogleJwtVerifier(googleOauthAPI);
    }

    @DisplayName("JWT Token 검증 로직 Test")
    @Test
    void jwtTokenTest() {
        Mockito.when(restTemplate.getForObject("https://www.googleapis.com/oauth2/v3/certs", String.class))
                .thenReturn(googlePublicKeys);

        boolean result = this.googleJwtVerifier.verify(new JWTToken(jwtToken));

        Assertions.assertTrue(result);
    }
}