package com.MindSpaceTeam.MindSpace.Service;

import com.MindSpaceTeam.MindSpace.Entity.UserWorkspace;
import com.MindSpaceTeam.MindSpace.Entity.UserWorkspaceId;
import com.MindSpaceTeam.MindSpace.Entity.Users;
import com.MindSpaceTeam.MindSpace.Entity.Workspace;
import com.MindSpaceTeam.MindSpace.Repository.UserRepository;
import com.MindSpaceTeam.MindSpace.Repository.UserWorkspaceRepository;
import com.MindSpaceTeam.MindSpace.Repository.WorkspaceRepository;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceCreateRequest;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WorkspaceService {
    private WorkspaceRepository workspaceRepository;
    private UserWorkspaceRepository userWorkspaceRepository;
    private UserRepository userRepository;
    private MongoOperations mongoTemplate;

    public WorkspaceService(WorkspaceRepository workspaceRepository, UserWorkspaceRepository userWorkspaceRepository, UserRepository userRepository, MongoOperations mongoTemplate) {
        this.workspaceRepository = workspaceRepository;
        this.userWorkspaceRepository = userWorkspaceRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public WorkspaceResponse createWorkspace(long userId, WorkspaceCreateRequest request) {
        Users user = userRepository.findById(userId).orElseThrow();
        Workspace workspace = new Workspace(request.getTitle());
        workspace = this.workspaceRepository.save(workspace);
        this.userWorkspaceRepository.save(new UserWorkspace(user, workspace, "owner"));

        mongoTemplate.createCollection(String.valueOf(workspace.getWorkspaceId()));

        return new WorkspaceResponse(workspace.getWorkspaceId(), workspace.getTitle(), workspace.getCreated());
    }

    public void deleteWorkspace(long userId, long workspaceId) throws Exception {
        UserWorkspaceId userWorkspaceId = new UserWorkspaceId(userId, workspaceId);

        workspaceRepository.deleteById(workspaceId);
        userWorkspaceRepository.deleteById(userWorkspaceId);
    }

    public void updateWorkspaceTitle(long workspaceId, String newTitle) throws Exception {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow();
        workspace.setTitle(newTitle);
    }
}
