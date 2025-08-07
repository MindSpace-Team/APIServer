package com.MindSpaceTeam.MindSpace.Components.Auth;

import com.MindSpaceTeam.MindSpace.Components.Auth.Type.OauthProvider;
import com.MindSpaceTeam.MindSpace.Exception.InvalidArgumentException;
import com.MindSpaceTeam.MindSpace.Exception.ProviderNotSupportedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OauthProviderMapping {

    // Google
    @Value("${oauth2.google.client-id}")
    private String googleClientId;
    @Value("${oauth2.google.redirect-url}")
    private String googleRedirectionUrl;
    private String scope = URLEncoder.encode("profile email", StandardCharsets.UTF_8);

    public String getOauthRedirectionUrl(OauthProvider oauthProvider, String state) {
         switch (oauthProvider) {
             case Google:
                 return getGoogleLoginRedirectionUrl(state);
             case Naver:
                 throw new ProviderNotSupportedException("Naver is not supported yet");
             case Kakao:
                 throw new ProviderNotSupportedException("Kakao is not supported yet");
         }

         throw new InvalidArgumentException("Unknown oauth provider: " + oauthProvider);
    }

    private String getGoogleLoginRedirectionUrl(String state) {
        return "https://accounts.google.com/o/oauth2/v2/auth?response_type=code&client_id=%s&scope=%s&state=%s&redirect_uri=%s"
                .formatted(googleClientId, scope, state, googleRedirectionUrl);
    }
}
