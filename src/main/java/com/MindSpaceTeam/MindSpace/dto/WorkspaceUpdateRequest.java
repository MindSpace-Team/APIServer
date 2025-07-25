package com.MindSpaceTeam.MindSpace.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceUpdateRequest {

    private String title;

    public WorkspaceUpdateRequest() {}

    public WorkspaceUpdateRequest(String title) {
        this.title = title;
    }
}
