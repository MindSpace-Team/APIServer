package com.MindSpaceTeam.MindSpace.Entity;

import jakarta.persistence.*;

@Entity(name="user_workspace")
@IdClass(UserWorkspaceId.class)
public class UserWorkspace {

    private long userId;
    private long workspaceId;

    @Id
    @ManyToOne
    @JoinColumn(name="userId")
    private Users users;

    @Id
    @ManyToOne
    @JoinColumn(name="workspaceId")
    private Workspace workspace;

    @Column
    private String role;
}
