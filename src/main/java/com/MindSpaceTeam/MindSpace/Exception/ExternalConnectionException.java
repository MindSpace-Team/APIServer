package com.MindSpaceTeam.MindSpace.Exception;

import com.MindSpaceTeam.MindSpace.Exception.Core.SystemException;

public class ExternalConnectionException extends SystemException {
    public ExternalConnectionException(String message) {
        super(message);
    }
}
