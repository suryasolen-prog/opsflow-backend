package opsflow_backend;

import opsflow_backend.entity.User;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CiTestDataInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public CiTestDataInitializer(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder) {

        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {

        String password =
                passwordEncoder.encode("AdminPassword123!");

        createUser(
                18L,
                "RBAC Admin",
                "rbacadmin2@opsflow.com",
                password,
                User.Role.ADMIN.name()
        );

        createUser(
                19L,
                "RBAC Manager",
                "rbacmanager@opsflow.com",
                password,
                User.Role.MANAGER.name()
        );

        createUser(
                20L,
                "RBAC Tester",
                "rbactester@opsflow.com",
                password,
                User.Role.TESTER.name()
        );

        createUser(
                21L,
                "RBAC Developer",
                "rbacdeveloper@opsflow.com",
                password,
                User.Role.DEVELOPER.name()
        );

        createUser(
                99L,
                "CI Test User",
                "ci-test-user-99@opsflow.com",
                password,
                User.Role.TESTER.name()
        );

        createProject();

        createProjectMember();

        resetSequence("users");
        resetSequence("projects");
        resetSequence("project_members");
    }

    private void createUser(
            Long id,
            String name,
            String email,
            String password,
            String role) {

        jdbcTemplate.update(
                """
                INSERT INTO users
                    (id, name, email, password, role, created_at, updated_at)
                VALUES
                    (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                ON CONFLICT DO NOTHING
                """,
                id,
                name,
                email,
                password,
                role
        );
    }

    private void createProject() {

        jdbcTemplate.update(
                """
                INSERT INTO projects
                    (id, name, description, status, created_at, updated_at)
                VALUES
                    (
                        1,
                        'CI Test Project',
                        'Project created for automated CI tests',
                        'ACTIVE',
                        CURRENT_TIMESTAMP,
                        CURRENT_TIMESTAMP
                    )
                ON CONFLICT DO NOTHING
                """
        );
    }

    private void createProjectMember() {

        jdbcTemplate.update(
                """
                INSERT INTO project_members
                    (id, project_id, user_id, role, created_at)
                VALUES
                    (
                        1,
                        1,
                        21,
                        'DEVELOPER',
                        CURRENT_TIMESTAMP
                    )
                ON CONFLICT DO NOTHING
                """
        );
    }

    private void resetSequence(String tableName) {

        jdbcTemplate.execute(
                """
                SELECT setval(
                    pg_get_serial_sequence('%s', 'id'),
                    GREATEST(
                        COALESCE((SELECT MAX(id) FROM %s), 1),
                        1
                    ),
                    true
                )
                """.formatted(tableName, tableName)
        );
    }
}