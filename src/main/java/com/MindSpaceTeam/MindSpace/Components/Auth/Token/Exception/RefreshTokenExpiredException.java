package com.MindSpaceTeam.MindSpace.Components.Auth.Token.Exception;

public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
