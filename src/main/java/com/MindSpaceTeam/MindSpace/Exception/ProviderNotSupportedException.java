package com.MindSpaceTeam.MindSpace.Exception;

import com.MindSpaceTeam.MindSpace.Exception.Core.ValidationException;

public class ProviderNotSupportedException extends ValidationException {
    public ProviderNotSupportedException(String message) {
        super(message);
    }
}
