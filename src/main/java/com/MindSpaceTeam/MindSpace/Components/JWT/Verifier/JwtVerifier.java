package com.MindSpaceTeam.MindSpace.Components.JWT.Verifier;

import com.MindSpaceTeam.MindSpace.Components.JWT.Type.OauthProvider;
import com.MindSpaceTeam.MindSpace.dto.JWTToken;
import org.springframework.stereotype.Component;

@Component
public interface JwtVerifier {
    public OauthProvider getOauthProvider();
    public boolean verify(JWTToken jwt);
}
