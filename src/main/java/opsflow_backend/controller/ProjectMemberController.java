package opsflow_backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import opsflow_backend.dto.AddProjectMemberRequest;
import opsflow_backend.dto.ProjectMemberResponse;
import opsflow_backend.entity.ProjectMember;
import opsflow_backend.service.ProjectAuthorizationService;
import opsflow_backend.service.ProjectMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
@SecurityRequirement(name = "bearerAuth")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;
    private final ProjectAuthorizationService authorizationService;

    public ProjectMemberController(
            ProjectMemberService projectMemberService,
            ProjectAuthorizationService authorizationService) {
        this.projectMemberService = projectMemberService;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public ResponseEntity<ProjectMemberResponse> addMember(
            @PathVariable Long projectId,
            @Valid @RequestBody AddProjectMemberRequest request,
            Authentication authentication) {

        if (!authorizationService.canAccessProject(projectId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return projectMemberService.addMember(projectId, request)
                .map(this::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProjectMemberResponse>> getProjectMembers(
            @PathVariable Long projectId,
            Authentication authentication) {

        if (!authorizationService.canAccessProject(projectId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<ProjectMemberResponse> members = projectMemberService.getProjectMembers(projectId)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            Authentication authentication) {

        if (!authorizationService.canAccessProject(projectId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean removed = projectMemberService.removeMember(projectId, memberId);

        if (!removed) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    private ProjectMemberResponse toResponse(ProjectMember member) {
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
