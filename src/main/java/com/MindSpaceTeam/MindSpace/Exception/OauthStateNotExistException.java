package com.MindSpaceTeam.MindSpace.Exception;

import com.MindSpaceTeam.MindSpace.Exception.Core.BuisinessException;

public class OauthStateNotExistException extends BuisinessException {
    public OauthStateNotExistException(String message) {
        super(message);
    }
}
