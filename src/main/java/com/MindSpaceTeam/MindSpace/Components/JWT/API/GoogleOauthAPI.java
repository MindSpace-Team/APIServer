package com.MindSpaceTeam.MindSpace.Components.JWT.API;

import com.MindSpaceTeam.MindSpace.Components.JWT.Type.OauthProvider;
import com.MindSpaceTeam.MindSpace.Components.JsonMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleOauthAPI implements Oauth2RequestAPI{
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${oauth2.google.redirection-url}")
    private String redirectUrl;
    @Value("${oauth2.google.publicKey-url}")
    private String GOOGLE_PUBLIC_KEY_REQUEST_URL;
    private static final String TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
    private final JsonMapper jsonMapper;

    public GoogleOauthAPI(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public OauthProvider getOauthProvider() {
        return OauthProvider.Google;
    }

    @Override
    public String requestOauth2AccessToken(String authorizationCode, String grantType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUrl);
        body.add("grant_type", grantType);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(TOKEN_REQUEST_URL, request, String.class);
    }

    @Override
    public JsonNode requestPublicKeys(String headerInfo) throws JsonProcessingException {
        JsonNode headerNode = this.jsonMapper.toJsonNode(headerInfo);

        RestTemplate restTemplate = new RestTemplate();

        String response = restTemplate.getForObject(GOOGLE_PUBLIC_KEY_REQUEST_URL, String.class);
        JsonNode keys = this.jsonMapper.toJsonNode(response).get("keys");
        JsonNode myKey = null;
        if (keys != null && keys.isArray()) {
            for (JsonNode key : keys) {
                if (key.get("kid").asText().equals(headerNode.get("kid").asText())) {
                    myKey = key;
                    break;
                }
            }
        }
        return myKey;
    }
}
