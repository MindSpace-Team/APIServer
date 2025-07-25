package com.MindSpaceTeam.MindSpace.Repository;

import com.MindSpaceTeam.MindSpace.Entity.UserWorkspace;
import com.MindSpaceTeam.MindSpace.Entity.UserWorkspaceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWorkspaceRepository extends JpaRepository<UserWorkspace, UserWorkspaceId> {
}
