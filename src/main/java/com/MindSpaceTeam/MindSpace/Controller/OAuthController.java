package com.MindSpaceTeam.MindSpace.Controller;

import com.MindSpaceTeam.MindSpace.Service.Oauth2UserService;
import com.MindSpaceTeam.MindSpace.Service.Result.LoginResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@RestController
public class OAuthController {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${oauth2.redirection-url}")
    private String redirectUrl;
    private String scope = URLEncoder.encode("profile email", StandardCharsets.UTF_8);
    private Oauth2UserService oauth2UserService;

    public OAuthController(Oauth2UserService oauth2UserService) {
        this.oauth2UserService = oauth2UserService;
    }

    @GetMapping("/oauth2/authorization/{provider}")
    public ResponseEntity<String> authorizePage(@PathVariable String provider, HttpServletRequest request) {
        String state = UUID.randomUUID().toString();
        HttpSession session = request.getSession();
        String redirectUrl = "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&" +
                "client_id=%s&scope=%s&state=%s&redirect_uri=%s".formatted(clientId, scope, state, this.redirectUrl);
        HttpHeaders headers = new HttpHeaders();

        session.setAttribute("oauth2_state", state);
        headers.setLocation(URI.create(redirectUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<String> processAuthorizationCode(HttpServletRequest request, @RequestParam("state") String state,
                                                           @RequestParam("code") String authorizationCode,
                                                           @RequestParam(value = "authuser", defaultValue = "0") String authUser,
                                                           @RequestParam(value = "prompt", defaultValue="none") String prompt) {
        HttpSession session = request.getSession();
        String savedState = Objects.toString(session.getAttribute("oauth2_state"));

        if (savedState == null || !savedState.equals(state)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        String requestUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUrl);
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(URI.create(requestUrl), requestEntity, String.class);
        String bodyData = response.getBody();

        LoginResult result = this.oauth2UserService.processLogin(bodyData);
        if (result == LoginResult.LOGIN_SUCCESS) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else if(result == LoginResult.SIGN_UP_SUCCESS) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
