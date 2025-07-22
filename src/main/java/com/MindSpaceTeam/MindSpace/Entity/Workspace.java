package com.MindSpaceTeam.MindSpace.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
public class Workspace {
    @Id
    @GeneratedValue
    @Column(name="workspaceId")
    private long workspaceId;
    @Column(name="title")
    private String title;
    @CreationTimestamp
    @Column(name="createdAt", updatable = false)
    private Instant createdAt;

    public Workspace(String title) {
        this.title = title;
    }

    public Workspace() { }
}
