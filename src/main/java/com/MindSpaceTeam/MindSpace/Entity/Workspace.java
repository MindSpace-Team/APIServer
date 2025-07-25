package com.MindSpaceTeam.MindSpace.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workspaceId;

    @Column
    private String title;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant created;

    public Workspace(String title) {
        this.title = title;
    }

    public Workspace() {}

    public Workspace(Long workpaceId, String title, Instant created) {
        this.workspaceId = workpaceId;
        this.title = title;
        this.created = created;
    }
}
