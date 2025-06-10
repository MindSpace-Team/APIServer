package com.MindSpaceTeam.MindSpace.Service;

import com.MindSpaceTeam.MindSpace.Components.JsonMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;


@Service
public class OauthJwtService {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${oauth2.redirection-url}")
    private String redirectUrl;
    private static final String TOKEN_REQUEST_URL = "https://oauth2.googleapis.com/token";
    private static final String PUBLICKEY_REQUEST_URL = "https://www.googleapis.com/oauth2/v3/certs";
    private JsonMapper jsonMapper;


    public OauthJwtService(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public String requestJwtToken(String authorizationCode, String grantType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUrl);
        body.add("grant_type", grantType);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);
        HttpEntity<String> response = restTemplate.postForEntity(TOKEN_REQUEST_URL, httpEntity, String.class);

        return response.getBody();
    }

    public boolean verifyToken(String jwtToken) {
        String[] sections = jwtToken.split("\\.");
        String header = sections[0];
        String payload = sections[1];
        String signature = sections[2];
        String headerInfo = decodeToken(header);
        try {
            JsonNode headerNode = jsonMapper.toJsonNode(headerInfo);
            String alg = headerNode.get("alg").asText();
            if (alg.equals("RS256")) {
                String serverSignature = hmacSha256(header + "." + payload);
                if (serverSignature.equals(signature)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return false;
    }

    public String requestOauthPublicKey() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(PUBLICKEY_REQUEST_URL, String.class);
        String data = response.getBody();
        JsonNode keys = this.jsonMapper.toJsonNode(data).get("keys");
        if (keys != null && keys.isArray()) {
            for (JsonNode key : keys) {
                if (key.get("alg").asText().equals("RS256")) {
                    return key.get("kid").asText();
                }
            }
        }
        return "no key";
    }

    public String decodeToken(String section) {
        Base64.Decoder decoder = Base64.getDecoder();

        return new String(decoder.decode(section));
    }

    public String hmacSha256(String message) throws Exception {
        String googlePublicKey = requestOauthPublicKey();
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(googlePublicKey.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secretKey);

        byte[] hash = sha256_HMAC.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    public String getProfileToken(String responseJson) throws JsonProcessingException {
        JsonNode node = this.jsonMapper.toJsonNode(responseJson);
        return node.get("id_token").asText();
    }

    public JsonNode getPayLoadJsonNode(String jwtToken) throws JsonProcessingException {
        String payloadSection = jwtToken.split("\\.")[1];
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(payloadSection));

        return this.jsonMapper.toJsonNode(payload);
    }

}
