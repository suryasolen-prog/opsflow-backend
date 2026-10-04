package opsflow_backend;

import opsflow_backend.entity.Project;
import opsflow_backend.entity.ProjectMember;
import opsflow_backend.repository.ProjectMemberRepository;
import opsflow_backend.repository.ProjectRepository;
import opsflow_backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectMemberControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    private static final String ADMIN_EMAIL =
            "rbacadmin2@opsflow.com";

    private static final String DEVELOPER_EMAIL =
            "rbacdeveloper@opsflow.com";

    private static final String TESTER_EMAIL =
            "rbactester@opsflow.com";


    @Test
    void adminCanGetProjectMembers() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        mockMvc.perform(
                        get("/api/projects/1/members")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                )
                .andExpect(status().isOk());
    }


    @Test
    void adminCanAddProjectMember() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        String request = """
                {
                    "userId": 99,
                    "role": "TESTER"
                }
                """;

        mockMvc.perform(
                        post("/api/projects/1/members")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                                .contentType(APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.projectId")
                        .value(1))
                .andExpect(jsonPath("$.userId")
                        .value(99))
                .andExpect(jsonPath("$.role")
                        .value("TESTER"));

        ProjectMember member =
                projectMemberRepository
                        .findByProjectIdAndUserId(1L, 99L)
                        .orElseThrow();

        projectMemberRepository.deleteById(member.getId());
    }


    @Test
    void duplicateProjectMemberReturnsConflict() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        String request = """
                {
                    "userId": 21,
                    "role": "DEVELOPER"
                }
                """;

        mockMvc.perform(
                        post("/api/projects/1/members")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                                .contentType(APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("User is already a member of this project"));
    }


    @Test
    void missingUserIdReturnsBadRequest() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        String request = """
                {
                    "role": "DEVELOPER"
                }
                """;

        mockMvc.perform(
                        post("/api/projects/1/members")
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
                .andExpect(jsonPath("$.fields.userId")
                        .value("User ID is required"));
    }


    @Test
    void missingRoleReturnsBadRequest() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        String request = """
                {
                    "userId": 21
                }
                """;

        mockMvc.perform(
                        post("/api/projects/1/members")
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
                .andExpect(jsonPath("$.fields.role")
                        .value("Role is required"));
    }


    @Test
    void nonexistentProjectReturnsNotFound() throws Exception {

        String adminToken =
                jwtService.generateToken(ADMIN_EMAIL);

        String request = """
                {
                    "userId": 21,
                    "role": "DEVELOPER"
                }
                """;

        mockMvc.perform(
                        post("/api/projects/999999999/members")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken
                                )
                                .contentType(APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void developerCannotManageProjectMembers() throws Exception {

        String developerToken =
                jwtService.generateToken(DEVELOPER_EMAIL);

        mockMvc.perform(
                        get("/api/projects/1/members")
                                .header(
                                        "Authorization",
                                        "Bearer " + developerToken
                                )
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void testerCannotManageProjectMembers() throws Exception {

        String testerToken =
                jwtService.generateToken(TESTER_EMAIL);

        mockMvc.perform(
                        get("/api/projects/1/members")
                                .header(
                                        "Authorization",
                                        "Bearer " + testerToken
                                )
                )
                .andExpect(status().isForbidden());
    }
}