package opsflow_backend;

import opsflow_backend.entity.Project;
import opsflow_backend.repository.ProjectRepository;
import opsflow_backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ProjectRepository projectRepository;

    private static final String ADMIN_EMAIL =
            "rbacadmin2@opsflow.com";

    private static final String MANAGER_EMAIL =
            "rbacmanager@opsflow.com";

    private static final String DEVELOPER_EMAIL =
            "rbacdeveloper@opsflow.com";

    private static final String TESTER_EMAIL =
            "rbactester@opsflow.com";


    @Test
    void adminCanCreateProject() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        String projectName =
                "AUTOTEST-CREATE-" + System.currentTimeMillis();

        String request = """
                {
                    "name": "%s",
                    "description": "Automated create test"
                }
                """.formatted(projectName);

        mockMvc.perform(
                        post("/api/projects")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                                .contentType(APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(projectName))
                .andExpect(jsonPath("$.description")
                        .value("Automated create test"))
                .andExpect(jsonPath("$.status")
                        .value("ACTIVE"));

        Project project =
                projectRepository.findAll()
                        .stream()
                        .filter(p -> projectName.equals(p.getName()))
                        .findFirst()
                        .orElseThrow();

        projectRepository.deleteById(project.getId());
    }


    @Test
    void adminCanGetAllProjects() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        mockMvc.perform(
                        get("/api/projects")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                )
                .andExpect(status().isOk());
    }


    @Test
    void adminCanGetProjectById() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        String projectName =
                "AUTOTEST-GET-" + System.currentTimeMillis();

        String request = """
                {
                    "name": "%s",
                    "description": "Automated get test"
                }
                """.formatted(projectName);

        mockMvc.perform(
                        post("/api/projects")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                                .contentType(APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated());

        Project project =
                projectRepository.findAll()
                        .stream()
                        .filter(p -> projectName.equals(p.getName()))
                        .findFirst()
                        .orElseThrow();

        try {
            mockMvc.perform(
                            get("/api/projects/" + project.getId())
                                    .header(
                                            "Authorization",
                                            "Bearer " + adminToken
                                    )
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id")
                            .value(project.getId()))
                    .andExpect(jsonPath("$.name")
                            .value(projectName));
        } finally {
            projectRepository.deleteById(project.getId());
        }
    }


    @Test
    void adminCanUpdateProject() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        String projectName =
                "AUTOTEST-UPDATE-" + System.currentTimeMillis();

        String createRequest = """
                {
                    "name": "%s",
                    "description": "Before update"
                }
                """.formatted(projectName);

        mockMvc.perform(
                        post("/api/projects")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                                .contentType(APPLICATION_JSON)
                                .content(createRequest)
                )
                .andExpect(status().isCreated());

        Project project =
                projectRepository.findAll()
                        .stream()
                        .filter(p -> projectName.equals(p.getName()))
                        .findFirst()
                        .orElseThrow();

        try {
            String updateRequest = """
                    {
                        "name": "AUTOTEST-UPDATED",
                        "description": "After update",
                        "status": "COMPLETED"
                    }
                    """;

            mockMvc.perform(
                            put("/api/projects/" + project.getId())
                                    .header(
                                            "Authorization",
                                            "Bearer " + adminToken
                                    )
                                    .contentType(APPLICATION_JSON)
                                    .content(updateRequest)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name")
                            .value("AUTOTEST-UPDATED"))
                    .andExpect(jsonPath("$.description")
                            .value("After update"))
                    .andExpect(jsonPath("$.status")
                            .value("COMPLETED"));
        } finally {
            projectRepository.deleteById(project.getId());
        }
    }


    @Test
    void invalidProjectDataReturnsBadRequest() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        String request = """
                {
                    "name": "",
                    "description": "Invalid project"
                }
                """;

        mockMvc.perform(
                        post("/api/projects")
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
                .andExpect(jsonPath("$.fields.name")
                        .value("Project name is required"));
    }


    @Test
    void nonexistentProjectReturnsNotFound() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        mockMvc.perform(
                        get("/api/projects/999999999")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void managerCanCreateProject() throws Exception {

        String managerToken =
                jwtService.generateToken(MANAGER_EMAIL);

        String projectName =
                "AUTOTEST-MANAGER-" + System.currentTimeMillis();

        String request = """
                {
                    "name": "%s",
                    "description": "Manager automated test"
                }
                """.formatted(projectName);

        mockMvc.perform(
                        post("/api/projects")
                                .header(
                                        "Authorization",
                                        "Bearer " + managerToken
                                )
                                .contentType(APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name")
                        .value(projectName));

        Project project =
                projectRepository.findAll()
                        .stream()
                        .filter(p -> projectName.equals(p.getName()))
                        .findFirst()
                        .orElseThrow();

        projectRepository.deleteById(project.getId());
    }


    @Test
    void developerCannotAccessProjects() throws Exception {

        String developerToken =
                jwtService.generateToken(DEVELOPER_EMAIL);

        mockMvc.perform(
                        get("/api/projects")
                                .header(
                                        "Authorization",
                                        "Bearer " + developerToken
                                )
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void testerCannotAccessProjects() throws Exception {

        String testerToken =
                jwtService.generateToken(TESTER_EMAIL);

        mockMvc.perform(
                        get("/api/projects")
                                .header(
                                        "Authorization",
                                        "Bearer " + testerToken
                                )
                )
                .andExpect(status().isForbidden());
    }
}