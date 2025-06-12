package com.MindSpaceTeam.MindSpace.dto;

import com.MindSpaceTeam.MindSpace.Entity.Users;
import org.springframework.stereotype.Component;

@Component
public class EntityConverter {

    public Users getUserEntity(UserInfoDto userInfo) {
        return Users.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .oauthProvider(userInfo.getOauthProvider())
                .role(userInfo.getRole())
                .build();
    }
}
