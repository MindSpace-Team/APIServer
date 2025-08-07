package com.MindSpaceTeam.MindSpace.Exception;

import com.MindSpaceTeam.MindSpace.Exception.Core.ValidationException;

public class InvalidJsonFormatException extends ValidationException {
    public InvalidJsonFormatException(String message) {
        super(message);
    }
}
