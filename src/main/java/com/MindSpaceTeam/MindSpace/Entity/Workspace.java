package com.MindSpaceTeam.MindSpace.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Getter
public class Workspace {
    @Id
    @GeneratedValue
    private long workspaceId;
    private String title;
    private long createdAt;

    public Workspace(String title) {
        this.title = title;
    }
}
