package com.MindSpaceTeam.MindSpace.dto.Login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Tokens {
    private String accessToken;
    private RefreshToken refreshToken;

}
