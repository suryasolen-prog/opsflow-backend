package opsflow_backend.dto;

import jakarta.validation.constraints.NotNull;
import opsflow_backend.entity.ProjectMember;

public class AddProjectMemberRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Role is required")
    private ProjectMember.MemberRole role;

    public AddProjectMemberRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ProjectMember.MemberRole getRole() {
        return role;
    }

    public void setRole(ProjectMember.MemberRole role) {
        this.role = role;
    }
}