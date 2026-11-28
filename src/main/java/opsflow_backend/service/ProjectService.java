package opsflow_backend.service;

import opsflow_backend.dto.CreateProjectRequest;
import opsflow_backend.dto.UpdateProjectRequest;
import opsflow_backend.entity.Project;
import opsflow_backend.repository.ProjectMemberRepository;
import opsflow_backend.repository.ProjectRepository;
import opsflow_backend.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProjectService {

    private static final String DUPLICATE_PROJECT_NAME = "OpsFlow Website Development";
    private static final String DUPLICATE_PROJECT_DESCRIPTION =
            "Planning, developing, testing and deploying the OpsFlow project management application.";

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    public Project createProject(CreateProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStatus(Project.Status.ACTIVE);
        return projectRepository.save(project);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    public Optional<Project> updateProject(Long id, UpdateProjectRequest request) {
        return projectRepository.findById(id)
                .map(project -> {
                    project.setName(request.getName());
                    project.setDescription(request.getDescription());
                    if (request.getStatus() != null) {
                        project.setStatus(request.getStatus());
                    }
                    return projectRepository.save(project);
                });
    }

    public boolean deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            return false;
        }
        projectRepository.deleteById(id);
        return true;
    }

    @Transactional
    public Map<String, Object> cleanupOpsFlowDuplicateProjects() {
        List<Project> matchingProjects =
                projectRepository.findByNameAndDescriptionOrderByCreatedAtAscIdAsc(
                        DUPLICATE_PROJECT_NAME, DUPLICATE_PROJECT_DESCRIPTION);

        if (matchingProjects.size() <= 1) {
            return cleanupResult(
                    matchingProjects.isEmpty() ? null : matchingProjects.get(0).getId(),
                    0,
                    "No duplicate projects found. Nothing was deleted.");
        }

        Project keepProject = matchingProjects.get(0);
        List<Project> duplicates = matchingProjects.subList(1, matchingProjects.size());

        // Abort the entire operation before deleting anything if a duplicate
        // contains tasks or members. Never cascade-delete related records.
        for (Project duplicate : duplicates) {
            if (taskRepository.existsByProjectId(duplicate.getId())
                    || projectMemberRepository.existsByProjectId(duplicate.getId())) {
                throw new IllegalStateException(
                        "Cleanup stopped safely: a duplicate project has tasks or members. No projects were deleted.");
            }
        }

        projectRepository.deleteAll(duplicates);

        return cleanupResult(
                keepProject.getId(),
                duplicates.size(),
                "Duplicate projects removed. The oldest matching project was kept.");
    }

    private Map<String, Object> cleanupResult(
            Long keptProjectId, int deletedCount, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("keptProjectId", keptProjectId);
        result.put("deletedCount", deletedCount);
        result.put("message", message);
        return result;
    }
}