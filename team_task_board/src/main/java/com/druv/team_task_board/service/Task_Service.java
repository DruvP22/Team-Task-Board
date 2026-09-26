package com.druv.team_task_board.service;

import com.druv.team_task_board.dto.Task_requestDTO;
import com.druv.team_task_board.dto.Task_responseDTO;
import com.druv.team_task_board.model.*;
import com.druv.team_task_board.repository.ProjectRepository;
import com.druv.team_task_board.repository.Task_Repository;
import com.druv.team_task_board.repository.User_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

//adding the below for the workflow logic 
import com.druv.team_task_board.exception.Invalid_Status_TransitionException;
import java.util.List;
import java.util.Map;

@Service 
public class Task_Service {
    
    private static final Map<Task_Status, List<Task_Status>> ALLOWED_TRANSITIONS = Map.of(
        Task_Status.BACKLOG, List.of(Task_Status.IN_PROGRESS),
        Task_Status.IN_PROGRESS, List.of(Task_Status.REVIEW, Task_Status.BACKLOG),
        Task_Status.REVIEW, List.of(Task_Status.DONE, Task_Status.IN_PROGRESS),
        Task_Status.DONE, List.of(Task_Status.REVIEW)
    );

    @Autowired
    private Task_Repository taskRepository;

    @Autowired 
    private ProjectRepository projectRepository;

    @Autowired 
    private User_Repository userRepository;

    @Autowired
private Slack_Notification_Service slackNotificationService;

    public Task_responseDTO create(Task_requestDTO dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setStatus(Task_Status.BACKLOG); // every new task starts in Backlog

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found: " + dto.getProjectId()));
        task.setProject(project);

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("User not found: " + dto.getAssigneeId()));
            task.setAssignnee(assignee);
        }
        Task saved = taskRepository.save(task);

        slackNotificationService.sendNotification(
        "🆕 New task created: *" + saved.getTitle() + "*" +
        (saved.getAssignnee() != null ? " — assigned to " + saved.getAssignnee().getEmail() : " — unassigned")
    );


        return toResponseDTO(saved);
    }

    public Page<Task_responseDTO> findAll(Pageable pageable) { 
        return taskRepository.findAll(pageable).map(this::toResponseDTO);

    }

    public Task_responseDTO findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));
        return toResponseDTO(task);
    }

    public Task_responseDTO update(Long id, Task_requestDTO dto) { 
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        return toResponseDTO(taskRepository.save(task));
    }

     public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    private Task_responseDTO toResponseDTO(Task task) {
        Task_responseDTO dto = new Task_responseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setAssigneeEmail(task.getAssignnee() != null ? task.getAssignnee().getEmail() : null);
        dto.setProjectId(task.getProject().getId());
        dto.setCreatedAt(task.getCreated_At());
        dto.setUpdatedAt(task.getUpdated_At());
        return dto;
    }

    public Task_responseDTO changeStatus(Long id, Task_Status newStatus) {
    Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found: " + id));

    Task_Status currentStatus = task.getStatus();

    if (currentStatus == newStatus) {
        throw new Invalid_Status_TransitionException(
                "Task is already in status " + currentStatus);
    
    
    }

    List<Task_Status> allowedNextStatuses = ALLOWED_TRANSITIONS.get(currentStatus);

    if (!allowedNextStatuses.contains(newStatus)) {
        throw new Invalid_Status_TransitionException(
                "Cannot move task from " + currentStatus + " to " + newStatus +
                ". Allowed next statuses: " + allowedNextStatuses);
    }

    task.setStatus(newStatus);
    Task saved = taskRepository.save(task);

    slackNotificationService.sendNotification(
        "🔄 Task *" + saved.getTitle() + "* moved to *" + newStatus + "*"
    );

    return toResponseDTO(saved);
}
}
