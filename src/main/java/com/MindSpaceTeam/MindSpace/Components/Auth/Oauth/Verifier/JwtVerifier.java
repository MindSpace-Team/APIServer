package com.MindSpaceTeam.MindSpace.Components.Auth.Oauth.Verifier;

import com.MindSpaceTeam.MindSpace.Components.Auth.Type.OauthProvider;
import com.MindSpaceTeam.MindSpace.dto.JWTToken;
import org.springframework.stereotype.Component;

@Component
public interface JwtVerifier {
    public OauthProvider getOauthProvider();
    public boolean verify(JWTToken jwt);
}
