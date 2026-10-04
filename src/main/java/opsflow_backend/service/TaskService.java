package opsflow_backend.service;

import opsflow_backend.dto.CreateTaskRequest;
import opsflow_backend.dto.UpdateTaskRequest;
import opsflow_backend.entity.Project;
import opsflow_backend.entity.Task;
import opsflow_backend.entity.User;
import opsflow_backend.repository.ProjectRepository;
import opsflow_backend.repository.TaskRepository;
import opsflow_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public Task createTask(Long projectId, CreateTaskRequest request) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        } else {
            task.setPriority(Task.Priority.MEDIUM);
        }

        task.setDueDate(request.getDueDate());
        task.setProject(project);

        if (request.getAssigneeId() != null) {

            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));

            task.setAssignee(assignee);
        }

        return taskRepository.save(task);
    }

    public List<Task> getTasksByProject(Long projectId) {

        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project not found");
        }

        return taskRepository.findByProjectId(projectId);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task updateTask(Long id, UpdateTaskRequest request) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        task.setDueDate(request.getDueDate());

        if (request.getAssigneeId() != null) {

            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));

            task.setAssignee(assignee);

        } else {

            task.setAssignee(null);
        }

        taskRepository.save(task);

        return taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    public boolean deleteTask(Long id) {

        if (!taskRepository.existsById(id)) {
            return false;
        }

        taskRepository.deleteById(id);
        return true;
    }
}