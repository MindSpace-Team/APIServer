package com.MindSpaceTeam.MindSpace.Components.JWT;

import com.MindSpaceTeam.MindSpace.Components.JWT.Type.OauthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OauthProviderMapping {

    // Google
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value("${oauth2.google.redirection-url}")
    private String googleRedirectionUrl;
    private String scope = URLEncoder.encode("profile email", StandardCharsets.UTF_8);

    public String getOauthRedirectionUrl(OauthProvider oauthProvider, String state) {
         switch (oauthProvider) {
             case Google:
                 return getGoogleLoginRedirectionUrl(state);
             case Naver:
                 return "null";
             case Kakao:
                 return "null";
         }

         throw new IllegalArgumentException("Unknown oauth provider: " + oauthProvider);
    }

    private String getGoogleLoginRedirectionUrl(String state) {
        return "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=%s&scope=%s&state=%s&redirect_uri=%s"
                .formatted(googleClientId, scope, state, googleRedirectionUrl);
    }
}
