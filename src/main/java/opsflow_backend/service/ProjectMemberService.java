package opsflow_backend.service;

import opsflow_backend.dto.AddProjectMemberRequest;
import opsflow_backend.entity.Project;
import opsflow_backend.entity.ProjectMember;
import opsflow_backend.entity.User;
import opsflow_backend.exception.DuplicateProjectMemberException;
import opsflow_backend.repository.ProjectMemberRepository;
import opsflow_backend.repository.ProjectRepository;
import opsflow_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectMemberService(
            ProjectMemberRepository projectMemberRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository) {

        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Optional<ProjectMember> addMember(
            Long projectId,
            AddProjectMemberRequest request) {

        Optional<Project> project =
                projectRepository.findById(projectId);

        Optional<User> user =
                userRepository.findById(request.getUserId());

        if (project.isEmpty() || user.isEmpty()) {
            return Optional.empty();
        }

        if (projectMemberRepository
                .existsByProjectIdAndUserId(
                        projectId,
                        request.getUserId())) {

            throw new DuplicateProjectMemberException(
                    "User is already a member of this project"
            );
        }

        ProjectMember member = new ProjectMember();

        member.setProject(project.get());
        member.setUser(user.get());
        member.setRole(request.getRole());

        return Optional.of(
                projectMemberRepository.save(member)
        );
    }

    public List<ProjectMember> getProjectMembers(
            Long projectId) {

        return projectMemberRepository
                .findByProjectId(projectId);
    }

    public boolean removeMember(Long memberId) {

        if (!projectMemberRepository.existsById(memberId)) {
            return false;
        }

        projectMemberRepository.deleteById(memberId);
        return true;
    }
}