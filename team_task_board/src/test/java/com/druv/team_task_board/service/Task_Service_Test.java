package com.druv.team_task_board.service;

import com.druv.team_task_board.exception.Invalid_Status_TransitionException;
import com.druv.team_task_board.model.Task;
import com.druv.team_task_board.model.Project;
import com.druv.team_task_board.model.Task_Status;
import com.druv.team_task_board.repository.ProjectRepository;
import com.druv.team_task_board.repository.Task_Repository;
import com.druv.team_task_board.repository.User_Repository;
import com.github.dockerjava.api.model.TaskStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Task_Service_Test {

    @Mock
    private Task_Repository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private User_Repository userRepository;

    @Mock
    private Slack_Notification_Service slackNotificationService;

    @InjectMocks
    private Task_Service taskService;

    private Task task;

    @BeforeEach
    void setUp() {

        Project project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        task = new Task();
        task.setId(1L);
        task.setTitle("Sample task");
        task.setStatus(Task_Status.BACKLOG);
        task.setProject(project);
    }

    @Test
    void changeStatus_legalTransition_updatesSuccessfully() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = taskService.changeStatus(1L, Task_Status.IN_PROGRESS);

        assertThat(result.getStatus()).isEqualTo(Task_Status.IN_PROGRESS);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void changeStatus_illegalSkip_throwsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.changeStatus(1L, Task_Status.DONE))
                .isInstanceOf(Invalid_Status_TransitionException.class)
                .hasMessageContaining("Cannot move task from BACKLOG to DONE");

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void changeStatus_sameStatus_throwsException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.changeStatus(1L, Task_Status.BACKLOG))
                .isInstanceOf(Invalid_Status_TransitionException.class)
                .hasMessageContaining("already in status");
    }

    @Test
    void changeStatus_terminalDoneState_rejectsAnyTransition() {
        task.setStatus(Task_Status.DONE);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.changeStatus(1L, Task_Status.IN_PROGRESS))
                .isInstanceOf(Invalid_Status_TransitionException.class);
    }

    @Test
    void changeStatus_taskNotFound_throwsException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.changeStatus(99L, Task_Status.IN_PROGRESS))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Task not found");
    }
}