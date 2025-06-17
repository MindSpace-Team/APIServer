package com.MindSpaceTeam.MindSpace.Controller;

import com.MindSpaceTeam.MindSpace.Components.JWT.OauthProviderMapping;
import com.MindSpaceTeam.MindSpace.Components.JWT.Type.OauthProvider;
import com.MindSpaceTeam.MindSpace.Service.Oauth2UserService;
import com.MindSpaceTeam.MindSpace.Service.Result.LoginResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
public class OAuthController {
    private Oauth2UserService oauth2UserService;
    private OauthProviderMapping oauth2ProviderMapping;

    public OAuthController(Oauth2UserService oauth2UserService, OauthProviderMapping oauth2ProviderMapping) {
        this.oauth2UserService = oauth2UserService;
        this.oauth2ProviderMapping = oauth2ProviderMapping;
    }

    @GetMapping("/oauth2/authorization/{provider}")
    public ResponseEntity<String> authorizePage(@PathVariable(value="provider") OauthProvider provider, HttpServletRequest request) {
        String state = UUID.randomUUID().toString();
        HttpSession session = request.getSession();
        String redirectUrl;
        try {
            redirectUrl = this.oauth2ProviderMapping.getOauthRedirectionUrl(provider, state);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Failed to get redirectUrl {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        HttpHeaders headers = new HttpHeaders();
        session.setAttribute("oauth2_state", state);
        headers.setLocation(URI.create(redirectUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/login/oauth2/code/{provider}")
    public ResponseEntity<String> processAuthorizationCode(HttpServletRequest request, @RequestParam("state") String state,
                                                           @RequestParam("code") String authorizationCode,
                                                           @RequestParam(value = "authuser", defaultValue = "0") String authUser,
                                                           @RequestParam(value = "prompt", defaultValue="none") String prompt,
                                                           @PathVariable(value = "provider") OauthProvider provider) {
        HttpSession session = request.getSession();
        String savedState = session.getAttribute("oauth2_state").toString();

        if (savedState == null) {
            log.warn("State code is not exist in session");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (!savedState.equals(state)) {
            log.warn("Failed to verify state code");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        LoginResult result = this.oauth2UserService.processLogin(authorizationCode, provider);
        if (result == LoginResult.LOGIN_SUCCESS) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else if(result == LoginResult.SIGN_UP_SUCCESS) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
