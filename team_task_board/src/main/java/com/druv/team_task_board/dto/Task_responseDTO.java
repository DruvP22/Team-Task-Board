package com.druv.team_task_board.dto;


import com.druv.team_task_board.model.Priority;
import com.druv.team_task_board.model.Task_Status;
import java.time.LocalDateTime;

public class Task_responseDTO {
    private Long id;
    private String title;
    private String description;
    private Task_Status status;
    private Priority priority;
    private String assigneeEmail;
    private Long projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Task_Status getStatus() {
        return status;
    }
    public void setStatus(Task_Status status) {
        this.status = status;
    }
    public Priority getPriority() {
        return priority;
    }
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    public String getAssigneeEmail() {
        return assigneeEmail;
    }
    public void setAssigneeEmail(String assigneeEmail) {
        this.assigneeEmail = assigneeEmail;
    }
    public Long getProjectId() {
        return projectId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    
}
