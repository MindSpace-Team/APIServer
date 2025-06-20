package com.MindSpaceTeam.MindSpace.Components.Auth.Token;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RefreshTokenizer {

    public String createRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
