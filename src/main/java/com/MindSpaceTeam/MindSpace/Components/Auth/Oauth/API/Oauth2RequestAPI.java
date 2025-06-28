package com.MindSpaceTeam.MindSpace.Components.Auth.Oauth.API;

import com.MindSpaceTeam.MindSpace.Components.Auth.Type.OauthProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface Oauth2RequestAPI {
    public OauthProvider getOauthProvider();
    public String requestOauth2AccessToken(String authorizationCode, String grantType);
    public JsonNode requestPublicKeys(String headerInfo) throws JsonProcessingException;
}
