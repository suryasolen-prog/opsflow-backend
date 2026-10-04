package opsflow_backend;

import opsflow_backend.entity.Project;
import opsflow_backend.entity.Task;
import opsflow_backend.repository.ProjectRepository;
import opsflow_backend.repository.TaskRepository;
import opsflow_backend.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    private static final String ADMIN_EMAIL =
            "rbacadmin2@opsflow.com";

    private final List<Long> testTaskIds = new ArrayList<>();

    @AfterEach
    void cleanUpTestTasks() {

        for (Long taskId : testTaskIds) {
            if (taskRepository.existsById(taskId)) {
                taskRepository.deleteById(taskId);
            }
        }

        testTaskIds.clear();
    }

    @Test
    void adminCanCreateTask() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        String request = """
                {
                    "title": "AUTOTEST-Create-Task",
                    "description": "Automated task test",
                    "priority": "HIGH",
                    "assigneeId": 21,
                    "dueDate": "2026-10-01"
                }
                """;

        MvcResult result = mockMvc.perform(
                        post("/api/projects/1/tasks")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                                .contentType(APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title")
                        .value("AUTOTEST-Create-Task"))
                .andExpect(jsonPath("$.status")
                        .value("TODO"))
                .andExpect(jsonPath("$.priority")
                        .value("HIGH"))
                .andExpect(jsonPath("$.projectId")
                        .value(1))
                .andExpect(jsonPath("$.assigneeId")
                        .value(21))
                .andReturn();

        String responseBody =
                result.getResponse().getContentAsString();

        Long createdTaskId =
                new com.fasterxml.jackson.databind.ObjectMapper()
                        .readTree(responseBody)
                        .get("id")
                        .asLong();

        testTaskIds.add(createdTaskId);
    }

    @Test
    void adminCanGetProjectTasks() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        mockMvc.perform(
                        get("/api/projects/1/tasks")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void adminCanGetTaskById() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        Task task = createTestTask(
                "AUTOTEST-Get-Task"
        );

        mockMvc.perform(
                        get("/api/tasks/" + task.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(task.getId().intValue()))
                .andExpect(jsonPath("$.title")
                        .value("AUTOTEST-Get-Task"))
                .andExpect(jsonPath("$.projectId")
                        .value(1));
    }

    @Test
    void adminCanUpdateTask() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        Task task = createTestTask(
                "AUTOTEST-Update-Task"
        );

        String request = """
                {
                    "title": "AUTOTEST-Updated-Task",
                    "description": "Updated automatically",
                    "status": "IN_PROGRESS",
                    "priority": "HIGH",
                    "assigneeId": 21,
                    "dueDate": "2026-10-05"
                }
                """;

        mockMvc.perform(
                        put("/api/tasks/" + task.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                                .contentType(APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(task.getId().intValue()))
                .andExpect(jsonPath("$.title")
                        .value("AUTOTEST-Updated-Task"))
                .andExpect(jsonPath("$.status")
                        .value("IN_PROGRESS"))
                .andExpect(jsonPath("$.priority")
                        .value("HIGH"))
                .andExpect(jsonPath("$.assigneeId")
                        .value(21))
                .andExpect(jsonPath("$.dueDate")
                        .value("2026-10-05"));
    }

    @Test
    void invalidTaskDataReturnsBadRequest() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        String request = """
                {
                    "title": "",
                    "description": "Invalid task"
                }
                """;

        mockMvc.perform(
                        post("/api/projects/1/tasks")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                                .contentType(APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Validation failed"))
                .andExpect(jsonPath("$.fields.title")
                        .value("Task title is required"));
    }

    @Test
    void nonexistentTaskReturnsNotFound() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        mockMvc.perform(
                        get("/api/tasks/999999999")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void managerCanAccessTasks() throws Exception {

        String managerToken =
                jwtService.generateToken(
                        "rbacmanager@opsflow.com"
                );

        mockMvc.perform(
                        get("/api/projects/1/tasks")
                                .header(
                                        "Authorization",
                                        "Bearer " + managerToken
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void developerCannotAccessTasks() throws Exception {

        String developerToken =
                jwtService.generateToken(
                        "rbacdeveloper@opsflow.com"
                );

        mockMvc.perform(
                        get("/api/projects/1/tasks")
                                .header(
                                        "Authorization",
                                        "Bearer " + developerToken
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void testerCannotAccessTasks() throws Exception {

        String testerToken =
                jwtService.generateToken(
                        "rbactester@opsflow.com"
                );

        mockMvc.perform(
                        get("/api/projects/1/tasks")
                                .header(
                                        "Authorization",
                                        "Bearer " + testerToken
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanDeleteTask() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        Task task = createTestTask(
                "AUTOTEST-Delete-Task"
        );

        mockMvc.perform(
                        delete("/api/tasks/" + task.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get("/api/tasks/" + task.getId())
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                )
                .andExpect(status().isNotFound());

        testTaskIds.remove(task.getId());
    }

    @Test
    void deleteNonexistentTaskReturnsNotFound()
            throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        mockMvc.perform(
                        delete("/api/tasks/999999999")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                )
                .andExpect(status().isNotFound());
    }

    private Task createTestTask(String title) {

        Project project =
                projectRepository.findById(1L)
                        .orElseThrow(
                                () -> new IllegalStateException(
                                        "Project 1 not found"
                                )
                        );

        Task task = new Task();

        task.setTitle(title);
        task.setDescription(
                "Temporary automated test task"
        );
        task.setStatus(Task.Status.TODO);
        task.setPriority(Task.Priority.MEDIUM);
        task.setProject(project);
        task.setDueDate(
                LocalDate.of(2026, 10, 1)
        );

        Task savedTask =
                taskRepository.save(task);

        testTaskIds.add(savedTask.getId());

        return savedTask;
    }
}