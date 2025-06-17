package com.MindSpaceTeam.MindSpace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class GoogleUserInfoDto implements UserInfoDto{
    private String email;
    private String name;
    private String oauthProvider;
    private String role;

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOauthProvider() {
        return oauthProvider;
    }

    @Override
    public String getRole() {
        return role;
    }
}
