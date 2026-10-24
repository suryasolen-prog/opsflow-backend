# OpsFlow Backend

OpsFlow is a Spring Boot backend application for managing users, projects, project members, and tasks with JWT authentication and role-based access control (RBAC).

## Technology Stack

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway database migrations
- JJWT for JWT authentication
- BCrypt password hashing
- Bean Validation
- SpringDoc OpenAPI / Swagger UI
- Maven
- JUnit 5 and Spring Boot Test

## Features

- User management
- JWT-based authentication
- Role-based access control
- Project and project-member management
- Task management
- Request validation
- Global exception handling
- Swagger/OpenAPI documentation
- Automated tests
- Versioned database schema migrations using Flyway

## User Roles and Access

OpsFlow supports four roles: `ADMIN`, `MANAGER`, `DEVELOPER`, and `TESTER`.

| Role | Projects | Project Members | Tasks | Users |
|---|---|---|---|---|
| ADMIN | Allowed | Allowed | Allowed | Allowed |
| MANAGER | Allowed | Allowed | Allowed | Denied |
| DEVELOPER | Denied | Denied | Denied | Denied |
| TESTER | Denied | Denied | Denied | Denied |

## Authentication

Login endpoint:

`POST /api/auth/login`

A successful login returns a JWT. Include it in requests to protected endpoints:

`Authorization: Bearer <JWT>`

Passwords are hashed using BCrypt.

## API Endpoints

### Authentication
- `POST /api/auth/login`

### Users
- `POST /api/users`
- `GET /api/users`
- `GET /api/users/{id}`
- `GET /api/users/email/{email}`
- `DELETE /api/users/{id}`
- `PUT /api/users/{id}/password`
- `GET /api/users/admin-test`

### Projects
- `POST /api/projects`
- `GET /api/projects`
- `GET /api/projects/{id}`
- `PUT /api/projects/{id}`
- `DELETE /api/projects/{id}`

### Project Members
- `POST /api/projects/{projectId}/members`
- `GET /api/projects/{projectId}/members`
- `DELETE /api/projects/{projectId}/members/{memberId}`

### Tasks
- `POST /api/projects/{projectId}/tasks`
- `GET /api/projects/{projectId}/tasks`
- `GET /api/tasks/{id}`
- `PUT /api/tasks/{id}`
- `DELETE /api/tasks/{id}`

## Configuration

Configure these environment variables before running the application:

- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

Use `.env.example` as a reference. Supply your own credentials and a strong JWT secret. Never commit real credentials, secrets, or tokens.

### Database Schema Management

The application uses PostgreSQL, Hibernate/JPA, and Flyway. Flyway manages database schema migrations, and Hibernate validates the schema at startup using `spring.jpa.hibernate.ddl-auto=validate`. Schema changes should be introduced through versioned Flyway migration scripts.

Flyway migration scripts are located in `src/main/resources/db/migration/`. The initial migration is `V1__initial_schema.sql`.

The current Flyway configuration includes `spring.flyway.baseline-on-migrate=true` and `spring.flyway.baseline-version=1`.

**Important:** For an existing non-empty database without Flyway history, baseline behavior may cause Flyway to skip the V1 migration. Verify the schema and migration history, and back up important data before making migration-related changes.

## Running the Application

From the project root, compile the application:

```powershell
.\mvnw.cmd clean compile
```

Run the tests and verification checks:

```powershell
.\mvnw.cmd clean verify
```

Start the application using your IDE's run configuration for `OpsflowBackendApplication`, or use the project's Maven configuration.

The default local application URL is:

`http://localhost:8080`

Ensure the required environment variables and PostgreSQL database are configured first.

## API Documentation

Swagger UI:

`http://localhost:8080/swagger-ui/index.html`

OpenAPI specification:

`http://localhost:8080/v3/api-docs`

## Health Check

Health endpoint:

`GET /actuator/health`

## Architecture

The application uses a layered Spring Boot structure:

- Controllers handle HTTP requests and responses.
- Services implement business logic.
- Repositories provide database access.
- Entities represent persisted data.
- DTOs define API request and response models.
- Security components handle JWT authentication and authorization.
- Exception handlers provide consistent error responses.

## Testing

Run the automated test suite with:

```powershell
.\mvnw.cmd clean verify
```

The last recorded successful verification completed with **54 tests, 0 failures, and 0 errors**. Rerun the command before relying on this count for a new release.

Tests cover authentication and security, user management, projects, project members, tasks, validation, and access control.

## Security Notes

- Keep database credentials and JWT secrets out of source control.
- Use strong, environment-specific secrets.
- Do not commit real JWT tokens.
- Treat test tokens as temporary credentials.
- Rotate any secret that may have been exposed.

## Project Status

The security fixes SEC-01 through SEC-05 have been merged into `main`. Run the verification suite before preparing a release.
