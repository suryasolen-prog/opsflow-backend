package opsflow_backend.dto;

import opsflow_backend.entity.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Task.Status status;
    private Task.Priority priority;
    private LocalDate dueDate;
    private Long projectId;
    private Long assigneeId;
    private String assigneeName;
    private String assigneeEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskResponse(
            Long id,
            String title,
            String description,
            Task.Status status,
            Task.Priority priority,
            LocalDate dueDate,
            Long projectId,
            Long assigneeId,
            String assigneeName,
            String assigneeEmail,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.projectId = projectId;
        this.assigneeId = assigneeId;
        this.assigneeName = assigneeName;
        this.assigneeEmail = assigneeEmail;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Task.Status getStatus() {
        return status;
    }

    public Task.Priority getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}