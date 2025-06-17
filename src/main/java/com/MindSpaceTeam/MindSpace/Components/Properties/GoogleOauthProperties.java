package com.MindSpaceTeam.MindSpace.Components.Properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "oauth2.google")
public class GoogleOauthProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private String scope;
    private String tokenUrl;
    private String publicKeyUrl;

    public GoogleOauthProperties() {}

    public GoogleOauthProperties(String clientId, String clientSecret, String redirectUrl, String scope, String tokenUrl, String publicKeyUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
        this.scope = scope;
        this.tokenUrl = tokenUrl;
        this.publicKeyUrl = publicKeyUrl;
    }
}
