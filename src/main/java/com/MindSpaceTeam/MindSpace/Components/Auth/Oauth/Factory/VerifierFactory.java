package com.MindSpaceTeam.MindSpace.Components.Auth.Oauth.Factory;

import com.MindSpaceTeam.MindSpace.Components.Auth.Type.OauthProvider;
import com.MindSpaceTeam.MindSpace.Components.Auth.Oauth.Verifier.JwtVerifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class VerifierFactory {
    private Map<OauthProvider, JwtVerifier> verifierMap;

    public VerifierFactory(List<JwtVerifier> verifiers) {
        verifierMap = verifiers.stream()
                .collect(Collectors.toMap(JwtVerifier::getOauthProvider, value -> value));
    }

    public JwtVerifier getVerifier(OauthProvider oauthProvider) {
        return verifierMap.get(oauthProvider);
    }

}
