package com.MindSpaceTeam.MindSpace.Exception;

import com.MindSpaceTeam.MindSpace.Exception.Core.BuisinessException;

public class OauthStateVerifyFailedException extends BuisinessException {
    public OauthStateVerifyFailedException(String message) {
        super(message);
    }
}
