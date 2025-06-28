package com.MindSpaceTeam.MindSpace.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WorkSpaceController {

    @GetMapping("/workspaces")
    public ResponseEntity<Object> getWorkSpaces() {
        return ResponseEntity.ok().build();
    }
}
