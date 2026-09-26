package com.druv.team_task_board.controller;

import com.druv.team_task_board.dto.StatusUpdate_requestDTO;
import com.druv.team_task_board.dto.Task_responseDTO;
import com.druv.team_task_board.model.Task_Status;
import com.druv.team_task_board.security.JwtUtil;
import com.druv.team_task_board.security.Jwt_Authentication_Filter;
import com.druv.team_task_board.security.Custom_User_Detail_Service;
import com.druv.team_task_board.service.Task_Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.druv.team_task_board.security.Jwt_Authentication_Filter;
import com.druv.team_task_board.security.Custom_User_Detail_Service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.context.annotation.Import;
import com.druv.team_task_board.security.Security_Config;

@WebMvcTest(Task_Controller.class)
@Import(Security_Config.class) 
public class Task_Controller_Test {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private Task_Service taskService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
private Jwt_Authentication_Filter jwtAuthenticationFilter;

@MockitoBean
private Custom_User_Detail_Service customUserDetailsService;

    @Test 
    @WithMockUser
    void changeStatus_validRequest_returns200() throws Exception {
        Task_responseDTO response = new Task_responseDTO();
        response.setId(1L);
        response.setStatus(Task_Status.IN_PROGRESS);

        when(taskService.changeStatus(1L, Task_Status.IN_PROGRESS)).thenReturn(response);

        StatusUpdate_requestDTO request = new StatusUpdate_requestDTO();
        request.setStatus(Task_Status.IN_PROGRESS);

        mockMvc.perform(patch("/api/tasks/1/status")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
