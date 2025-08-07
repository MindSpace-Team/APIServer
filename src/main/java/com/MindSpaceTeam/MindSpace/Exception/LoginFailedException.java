package com.MindSpaceTeam.MindSpace.Exception;

import com.MindSpaceTeam.MindSpace.Exception.Core.BuisinessException;

public class LoginFailedException extends BuisinessException {
    public LoginFailedException(String message) {
        super(message);
    }
}
