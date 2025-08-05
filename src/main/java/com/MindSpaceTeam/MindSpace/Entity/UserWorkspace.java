package com.MindSpaceTeam.MindSpace.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity(name="user_workspace")
@IdClass(UserWorkspaceId.class)
public class UserWorkspace {
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

    public UserWorkspace(Users users, Workspace workspace, String role) {
        this.users = users;
        this.workspace = workspace;
        this.role = role;
    }

    public UserWorkspace() {}
}
