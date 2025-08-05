package com.MindSpaceTeam.MindSpace.Entity;

import java.io.Serializable;
import java.util.Objects;

public class UserWorkspaceId implements Serializable {
    private long users;
    private long workspace;

    public UserWorkspaceId() {}

    public UserWorkspaceId(long users, long workspace) {
        this.users = users;
        this.workspace = workspace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserWorkspaceId)) return false;
        UserWorkspaceId that = (UserWorkspaceId) o;
        return Objects.equals(users, that.users) &&
                Objects.equals(workspace, that.workspace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(users, workspace);
    }
}
