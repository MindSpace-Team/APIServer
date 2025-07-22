package com.MindSpaceTeam.MindSpace.Controller;

import com.MindSpaceTeam.MindSpace.Config.SecurityConfig;import com.MindSpaceTeam.MindSpace.Service.WorkspaceService;
import com.MindSpaceTeam.MindSpace.TestSecurityConfig;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceCreateRequest;
import com.MindSpaceTeam.MindSpace.dto.WorkspaceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = WorkSpaceController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
)
@Import(TestSecurityConfig.class)
class WorkSpaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    StringRedisTemplate redisTemplate;
    @MockitoBean
    private WorkspaceService workspaceService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createWorkspaceTest() throws Exception {
        WorkspaceCreateRequest request = new WorkspaceCreateRequest("title1");
        WorkspaceResponse response = new WorkspaceResponse(1, "title1", 123123123);

        Mockito.when(this.workspaceService.createWorkspace(ArgumentMatchers.any(WorkspaceCreateRequest.class)))
                        .thenReturn(response);

        mockMvc.perform(post("/workspace")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void createWorkSpaceExceptionTest() throws Exception {
        String body = "{ \"title\": \"title1\"}";

        Mockito.doThrow(new DataAccessResourceFailureException("DB douwn"))
                .when(workspaceService);

        mockMvc.perform(post("/workspace")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                .andExpect(status().isInternalServerError());
    }
}