package com.MindSpaceTeam.MindSpace.dto;

import lombok.Getter;

@Getter
public class JWTToken {
    private String header;
    private String payload;
    private String signature;

    public JWTToken(String jwtToken) {
        String[] sections = jwtToken.split("\\.");
        this.header = sections[0];
        this.payload = sections[1];
        this.signature = sections[2];
    }
}
