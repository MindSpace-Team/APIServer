package com.MindSpaceTeam.MindSpace.Repository;

import com.MindSpaceTeam.MindSpace.Entity.UserWorkspace;
import com.MindSpaceTeam.MindSpace.Entity.UserWorkspaceId;
import com.MindSpaceTeam.MindSpace.Entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserWorkspaceRepository extends JpaRepository<UserWorkspace, UserWorkspaceId> {

    @Query("SELECT uw.workspace FROM user_workspace uw WHERE uw.users.userId = :userId")
    List<Workspace> findWorkspacesByUserId(@Param("userId") Long userId);

}
