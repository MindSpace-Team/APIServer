package com.MindSpaceTeam.MindSpace.Components.JWT.API;

import com.MindSpaceTeam.MindSpace.Components.JWT.Type.OauthProvider;
import com.MindSpaceTeam.MindSpace.Components.JsonMapper;
import com.MindSpaceTeam.MindSpace.Components.Properties.GoogleOauthProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class GoogleOauthAPI implements Oauth2RequestAPI{
    private GoogleOauthProperties properties;
    private final JsonMapper jsonMapper;
    private final RestTemplate restTemplate;

    public GoogleOauthAPI(JsonMapper jsonMapper, RestTemplate restTemplate, GoogleOauthProperties properties) {
        this.jsonMapper = jsonMapper;
        this.restTemplate = restTemplate;
        this.properties = properties;
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
        body.add("client_id", properties.getClientId());
        body.add("client_secret", properties.getClientSecret());
        body.add("redirect_uri", properties.getRedirectUrl());
        body.add("grant_type", grantType);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(properties.getTokenUrl(), request, String.class);
    }

    @Override
    public JsonNode requestPublicKeys(String headerInfo) throws JsonProcessingException {
        JsonNode headerNode = this.jsonMapper.toJsonNode(headerInfo);

        String response = restTemplate.getForObject(properties.getPublicKeyUrl(), String.class);
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
