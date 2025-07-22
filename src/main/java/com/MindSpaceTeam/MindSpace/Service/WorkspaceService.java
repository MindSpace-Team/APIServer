package com.MindSpaceTeam.MindSpace.Service;

import com.MindSpaceTeam.MindSpace.Entity.Workspace;
import com.MindSpaceTeam.MindSpace.Repository.WorkspaceRepository;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceCreateRequest;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceResponse;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceService {
    private WorkspaceRepository workspaceRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    public WorkspaceResponse createWorkspace(WorkspaceCreateRequest request) {
        Workspace workspace = new Workspace(request.getTitle());
        workspace = this.workspaceRepository.save(workspace);
        return new WorkspaceResponse(workspace.getWorkspaceId(), workspace.getTitle(), workspace.getCreatedAt());
    }
}
