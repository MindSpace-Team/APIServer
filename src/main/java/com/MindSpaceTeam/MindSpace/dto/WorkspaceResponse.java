package com.MindSpaceTeam.MindSpace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
public class WorkspaceResponse {
    private long workspaceId;
    private String title;
    private Instant createdAt;

    public WorkspaceResponse(){}
}
