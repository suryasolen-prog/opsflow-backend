package opsflow_backend.service;

import opsflow_backend.entity.Project;
import opsflow_backend.entity.ProjectMember;
import opsflow_backend.entity.User;
import opsflow_backend.repository.ProjectMemberRepository;
import opsflow_backend.repository.ProjectRepository;
import opsflow_backend.repository.TaskRepository;
import opsflow_backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectAuthorizationService {

    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public ProjectAuthorizationService(
            UserRepository userRepository,
            ProjectMemberRepository projectMemberRepository,
            TaskRepository taskRepository,
            ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    public List<Project> getAccessibleProjects(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return List.of();
        }

        if (isAdmin(authentication)) {
            return projectRepository.findAll();
        }

        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null || user.getRole() != User.Role.MANAGER) {
            return List.of();
        }

        return projectMemberRepository
                .findByUserIdAndRole(user.getId(), ProjectMember.MemberRole.MANAGER)
                .stream()
                .map(ProjectMember::getProject)
                .distinct()
                .toList();
    }

    public boolean canAccessTask(Long taskId, Authentication authentication) {
        return taskRepository.findById(taskId)
                .map(task -> canAccessProject(task.getProject().getId(), authentication))
                .orElse(false);
    }

    public boolean canAccessProject(Long projectId, Authentication authentication) {
        if (projectId == null || authentication == null
                || !authentication.isAuthenticated()) {
            return false;
        }

        if (isAdmin(authentication)) {
            return true;
        }

        User user = userRepository.findByEmail(authentication.getName()).orElse(null);
        if (user == null || user.getRole() != User.Role.MANAGER) {
            return false;
        }

        return projectMemberRepository.findByProjectIdAndUserId(projectId, user.getId())
                .filter(member -> member.getRole() == ProjectMember.MemberRole.MANAGER)
                .isPresent();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
