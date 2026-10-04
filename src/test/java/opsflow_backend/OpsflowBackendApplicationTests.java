package opsflow_backend;

import opsflow_backend.entity.User;
import opsflow_backend.repository.UserRepository;
import opsflow_backend.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class OpsflowBackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtService jwtService;

	private static final String ADMIN_EMAIL =
			"rbacadmin2@opsflow.com";

	private static final String DEVELOPER_EMAIL =
			"rbacdeveloper@opsflow.com";


	/*
	 * ============================================================
	 * BASIC APPLICATION TEST
	 * ============================================================
	 */

	@Test
	void contextLoads() {
	}


	/*
	 * ============================================================
	 * JWT / RBAC TESTS
	 * ============================================================
	 */

	@Test
	void adminCanAccessAdminTest() throws Exception {

		String adminToken =
				jwtService.generateToken(ADMIN_EMAIL);

		mockMvc.perform(
						get("/api/users/admin-test")
								.header(
										"Authorization",
										"Bearer " + adminToken
								)
				)
				.andExpect(status().isOk());
	}


	@Test
	void developerCannotAccessAdminTest() throws Exception {

		String developerToken =
				jwtService.generateToken(DEVELOPER_EMAIL);

		mockMvc.perform(
						get("/api/users/admin-test")
								.header(
										"Authorization",
										"Bearer " + developerToken
								)
				)
				.andExpect(status().isForbidden());
	}


	@Test
	void adminCanGetUsers() throws Exception {

		String adminToken =
				jwtService.generateToken(ADMIN_EMAIL);

		mockMvc.perform(
						get("/api/users")
								.header(
										"Authorization",
										"Bearer " + adminToken
								)
				)
				.andExpect(status().isOk());
	}


	@Test
	void developerCannotGetUsers() throws Exception {

		String developerToken =
				jwtService.generateToken(DEVELOPER_EMAIL);

		mockMvc.perform(
						get("/api/users")
								.header(
										"Authorization",
										"Bearer " + developerToken
								)
				)
				.andExpect(status().isForbidden());
	}


	@Test
	void unauthenticatedUserCannotGetUsers() throws Exception {

		mockMvc.perform(
						get("/api/users")
				)
				.andExpect(status().isForbidden());
	}


	@Test
	void invalidTokenCannotGetUsers() throws Exception {

		mockMvc.perform(
						get("/api/users")
								.header(
										"Authorization",
										"Bearer definitely-not-a-valid-jwt"
								)
				)
				.andExpect(status().isForbidden());
	}


	@Test
	void malformedJwtCannotGetUsers() throws Exception {

		mockMvc.perform(
						get("/api/users")
								.header(
										"Authorization",
										"Bearer abc"
								)
				)
				.andExpect(status().isForbidden());
	}


	/*
	 * ============================================================
	 * LOGIN TESTS
	 * ============================================================
	 */

	@Test
	void validAdminLoginReturnsJwt() throws Exception {

		String loginRequest = """
                {
                    "email": "rbacadmin2@opsflow.com",
                    "password": "AdminPassword123!"
                }
                """;

		mockMvc.perform(
						post("/api/auth/login")
								.contentType(APPLICATION_JSON)
								.content(loginRequest)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isNotEmpty());
	}


	@Test
	void invalidPasswordReturnsUnauthorized() throws Exception {

		String loginRequest = """
                {
                    "email": "rbacadmin2@opsflow.com",
                    "password": "definitely-wrong-password"
                }
                """;

		mockMvc.perform(
						post("/api/auth/login")
								.contentType(APPLICATION_JSON)
								.content(loginRequest)
				)
				.andExpect(status().isUnauthorized());
	}


	@Test
	void unknownEmailReturnsUnauthorized() throws Exception {

		String loginRequest = """
                {
                    "email": "does-not-exist@opsflow.com",
                    "password": "some-password"
                }
                """;

		mockMvc.perform(
						post("/api/auth/login")
								.contentType(APPLICATION_JSON)
								.content(loginRequest)
				)
				.andExpect(status().isUnauthorized());
	}


	@Test
	void loginWithoutEmailReturnsBadRequest() throws Exception {

		String loginRequest = """
                {
                    "password": "some-password"
                }
                """;

		mockMvc.perform(
						post("/api/auth/login")
								.contentType(APPLICATION_JSON)
								.content(loginRequest)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error")
						.value("Validation failed"))
				.andExpect(jsonPath("$.fields.email")
						.value("Email is required"));
	}


	@Test
	void loginWithoutPasswordReturnsBadRequest() throws Exception {

		String loginRequest = """
                {
                    "email": "rbacadmin2@opsflow.com"
                }
                """;

		mockMvc.perform(
						post("/api/auth/login")
								.contentType(APPLICATION_JSON)
								.content(loginRequest)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error")
						.value("Validation failed"))
				.andExpect(jsonPath("$.fields.password")
						.value("Password is required"));
	}


	/*
	 * ============================================================
	 * CREATE USER TESTS
	 * ============================================================
	 */

	@Test
	void adminCanCreateUser() throws Exception {

		String adminToken =
				jwtService.generateToken(ADMIN_EMAIL);

		String email =
				"testuser-" + System.currentTimeMillis()
						+ "@opsflow.com";

		String createRequest = """
                {
                    "name": "Test User",
                    "email": "%s",
                    "password": "TestPassword123!",
                    "role": "DEVELOPER"
                }
                """.formatted(email);

		mockMvc.perform(
						post("/api/users")
								.header(
										"Authorization",
										"Bearer " + adminToken
								)
								.contentType(APPLICATION_JSON)
								.content(createRequest)
				)
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.email").value(email))
				.andExpect(jsonPath("$.name").value("Test User"))
				.andExpect(jsonPath("$.role").value("DEVELOPER"))
				.andExpect(jsonPath("$.password").doesNotExist());
	}


	@Test
	void duplicateEmailReturnsConflict() throws Exception {

		String adminToken =
				jwtService.generateToken(ADMIN_EMAIL);

		String email =
				"duplicate-" + System.currentTimeMillis()
						+ "@opsflow.com";

		String createRequest = """
                {
                    "name": "Duplicate Test",
                    "email": "%s",
                    "password": "TestPassword123!",
                    "role": "DEVELOPER"
                }
                """.formatted(email);

		mockMvc.perform(
						post("/api/users")
								.header(
										"Authorization",
										"Bearer " + adminToken
								)
								.contentType(APPLICATION_JSON)
								.content(createRequest)
				)
				.andExpect(status().isCreated());

		mockMvc.perform(
						post("/api/users")
								.header(
										"Authorization",
										"Bearer " + adminToken
								)
								.contentType(APPLICATION_JSON)
								.content(createRequest)
				)
				.andExpect(status().isConflict());
	}


	@Test
	void developerCannotCreateUser() throws Exception {

		String developerToken =
				jwtService.generateToken(DEVELOPER_EMAIL);

		String createRequest = """
                {
                    "name": "Blocked User",
                    "email": "blocked-%s@opsflow.com",
                    "password": "TestPassword123!",
                    "role": "DEVELOPER"
                }
                """.formatted(System.currentTimeMillis());

		mockMvc.perform(
						post("/api/users")
								.header(
										"Authorization",
										"Bearer " + developerToken
								)
								.contentType(APPLICATION_JSON)
								.content(createRequest)
				)
				.andExpect(status().isForbidden());
	}


	@Test
	void invalidUserDataReturnsBadRequest() throws Exception {

		String adminToken =
				jwtService.generateToken(ADMIN_EMAIL);

		String createRequest = """
                {
                    "name": "",
                    "email": "",
                    "password": "",
                    "role": "DEVELOPER"
                }
                """;

		mockMvc.perform(
						post("/api/users")
								.header(
										"Authorization",
										"Bearer " + adminToken
								)
								.contentType(APPLICATION_JSON)
								.content(createRequest)
				)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error")
						.value("Validation failed"))
				.andExpect(jsonPath("$.fields.name")
						.value("Name is required"))
				.andExpect(jsonPath("$.fields.email")
						.value("Email is required"))
				.andExpect(jsonPath("$.fields.password")
						.value("Password is required"));
	}


	@Test
	void createdUserPasswordIsEncrypted() throws Exception {

		String adminToken =
				jwtService.generateToken(ADMIN_EMAIL);

		String email =
				"password-test-" + System.currentTimeMillis()
						+ "@opsflow.com";

		String plainPassword = "TestPassword123!";

		String createRequest = """
                {
                    "name": "Password Test User",
                    "email": "%s",
                    "password": "%s",
                    "role": "DEVELOPER"
                }
                """.formatted(email, plainPassword);

		mockMvc.perform(
						post("/api/users")
								.header(
										"Authorization",
										"Bearer " + adminToken
								)
								.contentType(APPLICATION_JSON)
								.content(createRequest)
				)
				.andExpect(status().isCreated());

		User savedUser =
				userRepository.findByEmail(email)
						.orElseThrow();

		assertNotEquals(
				plainPassword,
				savedUser.getPassword()
		);
	}


	/*
	 * ============================================================
	 * DELETE USER TESTS
	 * ============================================================
	 */

	@Test
	void adminCanDeleteUser() throws Exception {

		String adminToken =
				jwtService.generateToken(ADMIN_EMAIL);

		String email =
				"delete-test-" + System.currentTimeMillis()
						+ "@opsflow.com";

		String createRequest = """
                {
                    "name": "Delete Test User",
                    "email": "%s",
                    "password": "TestPassword123!",
                    "role": "DEVELOPER"
                }
                """.formatted(email);

		mockMvc.perform(
						post("/api/users")
								.header(
										"Authorization",
										"Bearer " + adminToken
								)
								.contentType(APPLICATION_JSON)
								.content(createRequest)
				)
				.andExpect(status().isCreated());

		User user =
				userRepository.findByEmail(email)
						.orElseThrow();

		mockMvc.perform(
						delete("/api/users/" + user.getId())
								.header(
										"Authorization",
										"Bearer " + adminToken
								)
				)
				.andExpect(status().isNoContent());

		assertTrue(
				userRepository.findById(user.getId()).isEmpty()
		);
	}


	@Test
	void deletingNonexistentUserReturnsNotFound() throws Exception {

		String adminToken =
				jwtService.generateToken(ADMIN_EMAIL);

		long nonexistentId = 999999999L;

		mockMvc.perform(
						delete("/api/users/" + nonexistentId)
								.header(
										"Authorization",
										"Bearer " + adminToken
								)
				)
				.andExpect(status().isNotFound());
	}


	@Test
	void developerCannotDeleteUser() throws Exception {

		String adminToken =
				jwtService.generateToken(ADMIN_EMAIL);

		String email =
				"developer-delete-" + System.currentTimeMillis()
						+ "@opsflow.com";

		String createRequest = """
                {
                    "name": "Developer Delete Test",
                    "email": "%s",
                    "password": "TestPassword123!",
                    "role": "DEVELOPER"
                }
                """.formatted(email);

		mockMvc.perform(
						post("/api/users")
								.header(
										"Authorization",
										"Bearer " + adminToken
								)
								.contentType(APPLICATION_JSON)
								.content(createRequest)
				)
				.andExpect(status().isCreated());

		User user =
				userRepository.findByEmail(email)
						.orElseThrow();

		String developerToken =
				jwtService.generateToken(DEVELOPER_EMAIL);

		mockMvc.perform(
						delete("/api/users/" + user.getId())
								.header(
										"Authorization",
										"Bearer " + developerToken
								)
				)
				.andExpect(status().isForbidden());
	}

	@Test
	void validJwtForNonexistentUserCannotGetUsers() throws Exception {

		String token =
				jwtService.generateToken(
						"user-does-not-exist-" + System.currentTimeMillis()
								+ "@opsflow.com"
				);

		mockMvc.perform(
						get("/api/users")
								.header(
										"Authorization",
										"Bearer " + token
								)
				)
				.andExpect(status().isForbidden());
	}


	@Test
	void unauthenticatedUserCannotCreateUser() throws Exception {

		String createRequest = """
            {
                "name": "Unauthorized User",
                "email": "unauthorized-%s@opsflow.com",
                "password": "TestPassword123!",
                "role": "DEVELOPER"
            }
            """.formatted(System.currentTimeMillis());

		mockMvc.perform(
						post("/api/users")
								.contentType(APPLICATION_JSON)
								.content(createRequest)
				)
				.andExpect(status().isForbidden());
	}


	@Test
	void unauthenticatedUserCannotDeleteUser() throws Exception {

		mockMvc.perform(
						delete("/api/users/1")
				)
				.andExpect(status().isForbidden());
	}
}