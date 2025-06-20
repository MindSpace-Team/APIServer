package com.MindSpaceTeam.MindSpace.dto.Login;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class RefreshToken {
    private String token;
    private Date exp;
}
