package opsflow_backend.dto;

import opsflow_backend.entity.ProjectMember;

import java.time.LocalDateTime;

public class ProjectMemberResponse {

    private Long id;
    private Long projectId;
    private Long userId;
    private String userName;
    private String userEmail;
    private ProjectMember.MemberRole role;
    private LocalDateTime createdAt;

    public ProjectMemberResponse(
            Long id,
            Long projectId,
            Long userId,
            String userName,
            String userEmail,
            ProjectMember.MemberRole role,
            LocalDateTime createdAt) {

        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.role = role;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public ProjectMember.MemberRole getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}