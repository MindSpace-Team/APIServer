package com.MindSpaceTeam.MindSpace.Exception;

import com.MindSpaceTeam.MindSpace.Exception.Core.ValidationException;

public class InvalidJwtTokenException extends ValidationException {
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
