package com.MindSpaceTeam.MindSpace.Controller;

import com.MindSpaceTeam.MindSpace.Service.WorkspaceService;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceCreateRequest;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class WorkSpaceController {
    WorkspaceService workspaceService;
    @Autowired
    ObjectMapper objectMapper;

    public WorkSpaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping("/workspaces")
    public ResponseEntity<Object> getWorkSpaces() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/workspace")
    public ResponseEntity<WorkspaceResponse> createWorkspace(@RequestBody WorkspaceCreateRequest request) throws JsonProcessingException {
        WorkspaceResponse workspaceResponseData = this.workspaceService.createWorkspace(request);

        return ResponseEntity
                .created(URI.create("/workspace/%s".formatted(workspaceResponseData.getWorkspaceId())))
                .build();
    }
}
