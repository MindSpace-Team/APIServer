package com.MindSpaceTeam.MindSpace.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceCreateRequest {
    private String title;

    public WorkspaceCreateRequest(){}

    public WorkspaceCreateRequest(String title) {
        this.title = title;
    }
}
