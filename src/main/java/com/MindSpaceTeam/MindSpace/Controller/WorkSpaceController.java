package com.MindSpaceTeam.MindSpace.Controller;

import com.MindSpaceTeam.MindSpace.Service.WorkspaceService;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceCreateRequest;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
public class WorkSpaceController {
    WorkspaceService workspaceService;

    public WorkSpaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @GetMapping("/workspaces")
    public ResponseEntity<Object> getWorkSpaces() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/workspace")
    public ResponseEntity<WorkspaceResponse> createWorkspace(@RequestBody WorkspaceCreateRequest body, HttpServletRequest request) throws JsonProcessingException {
        HttpSession session = request.getSession();
        long userId = (Long) session.getAttribute("userId");
        WorkspaceResponse workspaceResponse;
        try {
            workspaceResponse = this.workspaceService.createWorkspace(userId, body);

            return ResponseEntity
                    .created(URI.create("/workspace/%s".formatted(workspaceResponse.getWorkspaceId())))
                    .build();
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }

    @DeleteMapping("/workspace/{workspaceId}")
    public ResponseEntity<Object> deleteWorkspace(@PathVariable("workspaceId") Long workspaceId, HttpServletRequest request) {
        HttpSession session = request.getSession();
        long userId = (Long) session.getAttribute("userId");
        try {
            this.workspaceService.deleteWorkspace(userId, workspaceId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .build();
        }
    }
}
