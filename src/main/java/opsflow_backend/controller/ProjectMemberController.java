package opsflow_backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import opsflow_backend.dto.AddProjectMemberRequest;
import opsflow_backend.dto.ProjectMemberResponse;
import opsflow_backend.entity.ProjectMember;
import opsflow_backend.service.ProjectMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@SecurityRequirement(name = "bearerAuth")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(
            ProjectMemberService projectMemberService) {

        this.projectMemberService = projectMemberService;
    }

    @PostMapping
    public ResponseEntity<ProjectMemberResponse> addMember(
            @PathVariable Long projectId,
            @Valid @RequestBody AddProjectMemberRequest request) {

        return projectMemberService
                .addMember(projectId, request)
                .map(this::toResponse)
                .map(response ->
                        ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response))
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    @GetMapping
    public ResponseEntity<List<ProjectMemberResponse>>
    getProjectMembers(
            @PathVariable Long projectId) {

        List<ProjectMemberResponse> members =
                projectMemberService
                        .getProjectMembers(projectId)
                        .stream()
                        .map(this::toResponse)
                        .toList();

        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId) {

        boolean removed =
                projectMemberService.removeMember(memberId);

        if (!removed) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    private ProjectMemberResponse toResponse(
            ProjectMember member) {

        return new ProjectMemberResponse(
                member.getId(),
                member.getProject().getId(),
                member.getUser().getId(),
                member.getUser().getName(),
                member.getUser().getEmail(),
                member.getRole(),
                member.getCreatedAt()
        );
    }
}