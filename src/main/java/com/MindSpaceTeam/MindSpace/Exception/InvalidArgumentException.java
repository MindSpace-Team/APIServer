package com.MindSpaceTeam.MindSpace.Exception;

import com.MindSpaceTeam.MindSpace.Exception.Core.ValidationException;

public class InvalidArgumentException extends ValidationException {
    public InvalidArgumentException(String message) {
        super(message);
    }
}
