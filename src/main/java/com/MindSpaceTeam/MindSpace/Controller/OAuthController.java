package com.MindSpaceTeam.MindSpace.Controller;

import com.MindSpaceTeam.MindSpace.Components.Auth.OauthProviderMapping;
import com.MindSpaceTeam.MindSpace.Components.Auth.Type.OauthProvider;
import com.MindSpaceTeam.MindSpace.Entity.Users;
import com.MindSpaceTeam.MindSpace.Service.Oauth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
public class OAuthController {
    @Value("oauth2.login.failed-url")
    private String loginFailedUrl;
    private Oauth2UserService oauth2UserService;
    private OauthProviderMapping oauth2ProviderMapping;

    public OAuthController(Oauth2UserService oauth2UserService, OauthProviderMapping oauth2ProviderMapping) {
        this.oauth2UserService = oauth2UserService;
        this.oauth2ProviderMapping = oauth2ProviderMapping;
    }

    @GetMapping("/oauth2/authorization/{provider}")
    public ResponseEntity<String> authorizePage(@PathVariable(value="provider") OauthProvider provider,
                                                @RequestParam("redirect_url") String successRedirectUrl,
                                                HttpServletRequest request) {
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
        session.setAttribute("successRedirectUrl", successRedirectUrl);
        headers.setLocation(URI.create(redirectUrl));
        log.info("Redirect to {} authorization page", provider.getProviderName());

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
        String successRedirectUrl = session.getAttribute("successRedirectUrl").toString();

        HttpHeaders headers = new HttpHeaders();
        if (savedState == null) {
            log.warn("State code is not exist in session");
            headers.setLocation(URI.create(loginFailedUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        if (!savedState.equals(state)) {
            log.warn("Failed to verify state code");
            headers.setLocation(URI.create(loginFailedUrl));
            return new ResponseEntity<>(headers, HttpStatus.FORBIDDEN);
        }

        String sessionId = session.getId();
        Users user = this.oauth2UserService.processLogin(authorizationCode, provider);
        session.setAttribute("sid", sessionId);
        session.setAttribute("userId", user.getUserId());

        ResponseCookie cookie = ResponseCookie.from("sid", sessionId)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(2 * 60 * 60)
                .sameSite("Strict")
                .build();

        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        headers.setLocation(URI.create("https://" + successRedirectUrl));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> processLogout(@CookieValue("sid") String sid, HttpServletRequest request) {
        request.getSession().invalidate();

        ResponseCookie cookie = ResponseCookie.from("sid", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body("로그아웃 성공");
    }

}
