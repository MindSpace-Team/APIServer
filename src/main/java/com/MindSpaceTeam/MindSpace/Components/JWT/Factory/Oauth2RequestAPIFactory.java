package com.MindSpaceTeam.MindSpace.Components.JWT.Factory;

import com.MindSpaceTeam.MindSpace.Components.JWT.API.Oauth2RequestAPI;
import com.MindSpaceTeam.MindSpace.Components.JWT.Type.OauthProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Oauth2RequestAPIFactory {
    private Map<OauthProvider, Oauth2RequestAPI> apis;

    public Oauth2RequestAPIFactory(List<Oauth2RequestAPI> apis) {
        this.apis = apis.stream()
                .collect(Collectors.toMap(Oauth2RequestAPI::getOauthProvider, value -> value));
    }

    public Oauth2RequestAPI getAPI(OauthProvider provider) {
        return apis.get(provider);
    }

}
