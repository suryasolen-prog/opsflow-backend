package opsflow_backend.service;

import opsflow_backend.dto.CreateProjectRequest;
import opsflow_backend.dto.UpdateProjectRequest;
import opsflow_backend.entity.Project;
import opsflow_backend.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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

    public Optional<Project> updateProject(
            Long id,
            UpdateProjectRequest request) {

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
}