package com.MindSpaceTeam.MindSpace.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long workspaceId;

    @Column
    private String title;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant created;

    @OneToMany(mappedBy="workspace")
    private List<UserWorkspace> userWorkspaceList;

    public Workspace(String title) {
        this.title = title;
    }

    public Workspace() {}
}
