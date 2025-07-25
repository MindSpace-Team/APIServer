package com.MindSpaceTeam.MindSpace.Controller;

import com.MindSpaceTeam.MindSpace.Config.SecurityConfig;
import com.MindSpaceTeam.MindSpace.Entity.Workspace;
import com.MindSpaceTeam.MindSpace.Service.WorkspaceService;
import com.MindSpaceTeam.MindSpace.TestSecurityConfig;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceCreateRequest;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = WorkSpaceController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
@Import(TestSecurityConfig.class)
class WorkSpaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StringRedisTemplate stringRedisTemplate;
    @MockitoBean
    private WorkspaceService workspaceService;
    @Autowired
    ObjectMapper objectMapper;

    static MockHttpSession session;

    @BeforeAll
    static void setup() {
        session = new MockHttpSession();
        session.setAttribute("userId", 123L);
    }

    @Test
    void createWorkspaceTest() throws Exception {
        WorkspaceCreateRequest request = new WorkspaceCreateRequest("title1");
        WorkspaceResponse response = new WorkspaceResponse(1, "title1", Instant.now());

        Mockito.when(this.workspaceService.createWorkspace(ArgumentMatchers.anyLong(), ArgumentMatchers.any(WorkspaceCreateRequest.class)))
                        .thenReturn(response);

        mockMvc.perform(post("/workspace")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void createWorkSpaceExceptionTest() throws Exception {
        String body = "{ \"title\": \"title1\" }";

        Mockito.doThrow(new DataAccessResourceFailureException("DB douwn"))
                .when(workspaceService)
                .createWorkspace(Mockito.anyLong(), Mockito.any(WorkspaceCreateRequest.class));

        mockMvc.perform(post("/workspace")
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteWorkspaceTest() throws Exception {
        final String workspaceId = "123";

        Mockito.doNothing().when(workspaceService).deleteWorkspace(Mockito.anyLong(), Mockito.anyLong());

        mockMvc.perform(delete("/workspace/" + workspaceId)
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteWorkspaceExceptionTest() throws Exception {
        final String workspaceId = "123";

        Mockito.doThrow(new Exception("DB down"))
                .when(workspaceService).deleteWorkspace(Mockito.anyLong(), Mockito.anyLong());

        mockMvc.perform(delete("/workspace/" + workspaceId)
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void patchWorkspaceTest() throws Exception {
        final String workspaceId = "123";
        final String new_title = "{ \"title\": \"new title\"}";

        Mockito.doNothing()
                .when(workspaceService).updateWorkspaceTitle(Mockito.anyLong(), Mockito.anyString());

        mockMvc.perform(patch("/workspace/" + workspaceId)
                    .session(session)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new_title))
                .andExpect(status().isOk());
    }

    @Test
    void patchWorkspaceExceptionTest() throws Exception {
        final String workspaceId = "123";
        final String new_title = "{ \"title\": \"new title\"}";

        Mockito.doThrow(new Exception("Internal Server Error"))
                .when(workspaceService).updateWorkspaceTitle(Mockito.anyLong(), Mockito.anyString());

        mockMvc.perform(patch("/workspace/" + workspaceId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new_title))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllWorkspaceTest() throws Exception {
        Instant now = Instant.now();

        String expectedJson = """
                [
                   { "workspaceId": 1, "title": "title1" },
                   { "workspaceId": 2, "title": "title2" }
                ]
                """;
        Mockito.doReturn(List.of(
                new Workspace(1L, "title1", now),
                new Workspace(2L, "title2", now))
        ).when(workspaceService).getAllWorkspaces(Mockito.anyLong());

        mockMvc.perform(get("/workspaces")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}