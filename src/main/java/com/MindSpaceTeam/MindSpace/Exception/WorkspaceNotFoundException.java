package com.MindSpaceTeam.MindSpace.Exception;

import com.MindSpaceTeam.MindSpace.Exception.Core.BuisinessException;

public class WorkspaceNotFoundException extends BuisinessException {
    public WorkspaceNotFoundException(String message) {
        super(message);
    }
}
