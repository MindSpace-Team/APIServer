package com.MindSpaceTeam.MindSpace.dto.Login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResult {
    private AccessToken accessToken;
    private RefreshToken refreshToken;

}
