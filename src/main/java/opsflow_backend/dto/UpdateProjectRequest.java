package opsflow_backend.dto;

import jakarta.validation.constraints.NotBlank;
import opsflow_backend.entity.Project;

public class UpdateProjectRequest {

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    private Project.Status status;

    public UpdateProjectRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Project.Status getStatus() {
        return status;
    }

    public void setStatus(Project.Status status) {
        this.status = status;
    }
}