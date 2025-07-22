package com.MindSpaceTeam.MindSpace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class WorkspaceResponse {
    private long workspaceId;
    private String title;
    private long createdAt;

    public WorkspaceResponse(){}
}
