package opsflow_backend.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import opsflow_backend.dto.CreateTaskRequest;
import opsflow_backend.dto.TaskResponse;
import opsflow_backend.dto.UpdateTaskRequest;
import opsflow_backend.entity.Task;
import opsflow_backend.service.ProjectAuthorizationService;
import opsflow_backend.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;
    private final ProjectAuthorizationService authorizationService;

    public TaskController(
            TaskService taskService,
            ProjectAuthorizationService authorizationService) {
        this.taskService = taskService;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication) {
        if (!authorizationService.canAccessProject(projectId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Task task = taskService.createTask(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toTaskResponse(task));
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks(
            @PathVariable Long projectId,
            Authentication authentication) {
        if (!authorizationService.canAccessProject(projectId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<TaskResponse> tasks = taskService.getTasksByProject(projectId)
                .stream().map(this::toTaskResponse).toList();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable Long id,
            Authentication authentication) {
        Task task = taskService.getTaskById(id).orElse(null);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        if (!authorizationService.canAccessTask(id, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(toTaskResponse(task));
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request,
            Authentication authentication) {
        if (taskService.getTaskById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!authorizationService.canAccessTask(id, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Task updatedTask = taskService.updateTask(id, request);
        return ResponseEntity.ok(toTaskResponse(updatedTask));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            Authentication authentication) {
        if (taskService.getTaskById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (!authorizationService.canAccessTask(id, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean deleted = taskService.deleteTask(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @SuppressWarnings("unused")
    private TaskResponse toTaskResponse(Task task) {
        Long assigneeId = null;
        String assigneeName = null;
        String assigneeEmail = null;

        if (task.getAssignee() != null) {
            assigneeId = task.getAssignee().getId();
            assigneeName = task.getAssignee().getName();
            assigneeEmail = task.getAssignee().getEmail();
        }

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getProject().getId(),
                assigneeId,
                assigneeName,
                assigneeEmail,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
