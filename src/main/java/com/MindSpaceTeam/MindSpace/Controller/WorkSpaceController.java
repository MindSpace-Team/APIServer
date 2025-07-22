package com.MindSpaceTeam.MindSpace.Controller;

import com.MindSpaceTeam.MindSpace.Service.WorkspaceService;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceCreateRequest;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class WorkSpaceController {
    private WorkspaceService workspaceService;

    public WorkSpaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping("/workspaces")
    public ResponseEntity<Object> getWorkSpaces() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/workspace")
    public ResponseEntity<WorkspaceResponse> createWorkspace(@RequestBody WorkspaceCreateRequest request) {
        try {
            WorkspaceResponse workspaceResponseData = this.workspaceService.createWorkspace(request);
            return ResponseEntity
                    .created(URI.create("/workspace/%s".formatted(workspaceResponseData.getWorkspaceId())))
                    .build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .build();
        }
    }
}
