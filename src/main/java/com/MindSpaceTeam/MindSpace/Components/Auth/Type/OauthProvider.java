package com.MindSpaceTeam.MindSpace.Components.Auth.Type;

public enum OauthProvider {
    Google("google"),
    Naver("naver"),
    Kakao("kakao");

    private String providerName;

    OauthProvider(String providerName) {
        this.providerName = providerName;
    }

    public static OauthProvider from(String providerName) {
        for (OauthProvider provider : OauthProvider.values()) {
            if (provider.providerName.equals(providerName)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown provider: " + providerName);
    }

    public String getProviderName() {
        return this.providerName;
    }
}
