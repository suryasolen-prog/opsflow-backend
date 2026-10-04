# OpsFlow Backend

OpsFlow is a Spring Boot backend application for managing users, projects, project members, and tasks with JWT authentication and role-based access control (RBAC).

## Technology Stack

* Java 21
* Spring Boot 4.1.1
* Spring Web MVC
* Spring Data JPA
* Spring Security
* PostgreSQL 18
* JWT authentication using JJWT
* BCrypt password hashing
* Bean Validation
* SpringDoc OpenAPI / Swagger UI
* Maven
* JUnit 5 / Spring Boot Test

## Main Features

* User management
* JWT-based authentication
* Role-based access control
* Project management
* Project member management
* Task management
* Request validation
* Global exception handling
* Swagger/OpenAPI documentation
* Automated integration tests

## User Roles

OpsFlow currently supports four roles:

* ADMIN
* MANAGER
* DEVELOPER
* TESTER

### Current Access Rules

| Role      | Projects | Project Members | Tasks   | Users   |
| --------- | -------- | --------------- | ------- | ------- |
| ADMIN     | Allowed  | Allowed         | Allowed | Allowed |
| MANAGER   | Allowed  | Allowed         | Allowed | Denied  |
| DEVELOPER | Denied   | Denied          | Denied  | Denied  |
| TESTER    | Denied   | Denied          | Denied  | Denied  |

## Authentication

Authentication is implemented using JSON Web Tokens (JWT).

The login endpoint is:

`POST /api/auth/login`

A successful login returns a JWT token.

Protected endpoints require:

`Authorization: Bearer <JWT>`

Passwords are stored using BCrypt hashing.

## API Modules

### Authentication

* `POST /api/auth/login`

### Users

* `POST /api/users`
* `GET /api/users`
* `GET /api/users/{id}`
* `GET /api/users/email/{email}`
* `DELETE /api/users/{id}`
* `PUT /api/users/{id}/password`
* `GET /api/users/admin-test`

### Projects

* `POST /api/projects`
* `GET /api/projects`
* `GET /api/projects/{id}`
* `PUT /api/projects/{id}`
* `DELETE /api/projects/{id}`

### Project Members

* `POST /api/projects/{projectId}/members`
* `GET /api/projects/{projectId}/members`
* `DELETE /api/projects/{projectId}/members/{memberId}`

### Tasks

* `POST /api/projects/{projectId}/tasks`
* `GET /api/projects/{projectId}/tasks`
* `GET /api/tasks/{id}`
* `PUT /api/tasks/{id}`
* `DELETE /api/tasks/{id}`

## Database

OpsFlow uses PostgreSQL.

Database configuration is provided through environment variables:

* `DB_USERNAME`
* `DB_PASSWORD`
* `JWT_SECRET`

Sensitive credentials should not be committed to source control.

The application uses Hibernate/JPA with:

`spring.jpa.hibernate.ddl-auto=update`

## Running the Application

### 1. Configure environment variables

Set:

* `DB_USERNAME`
* `DB_PASSWORD`
* `JWT_SECRET`

### Environment Variable Example

Copy `.env.example` as a reference and provide your own values for:

- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

Never commit real credentials or JWT secrets.

### Maven Commands

Compile the application:

```text
.\mvnw.cmd clean compile


---

### 9. Add the application architecture section

Add:

```markdown
## Architecture

OpsFlow follows a layered Spring Boot architecture:

- Controllers handle HTTP requests and responses.
- Services contain business logic.
- Repositories provide database access.
- Entities represent database tables.
- DTOs define API request and response models.
- Security handles JWT authentication and RBAC.
- Exception handlers provide consistent API error responses.

## Common HTTP Responses

- `200 OK` — successful read/update operation
- `201 Created` — successful creation
- `204 No Content` — successful deletion
- `400 Bad Request` — validation failure
- `401 Unauthorized` — missing or invalid authentication
- `403 Forbidden` — authenticated user lacks permission
- `404 Not Found` — requested resource does not exist
- `409 Conflict` — duplicate resource

### 2. Start the application

Run:

`OpsflowBackendApplication`

The backend runs on:

`http://localhost:8080`

## Swagger UI

Swagger UI is available at:

`http://localhost:8080/swagger-ui/index.html`

OpenAPI documentation is available at:

`http://localhost:8080/v3/api-docs`

## Health Check

The application exposes:

`GET /actuator/health`

## Project Structure

```text
opsflow-backend
├── src
│   ├── main
│   │   ├── java
│   │   │   └── opsflow_backend
│   │   │       ├── auth
│   │   │       ├── config
│   │   │       ├── controller
│   │   │       ├── dto
│   │   │       ├── entity
│   │   │       ├── exception
│   │   │       ├── repository
│   │   │       ├── security
│   │   │       └── service
│   │   └── resources
│   │       └── application.properties
│   └── test
│       └── java
│           └── opsflow_backend
├── pom.xml
└── README.md
```

## Testing

The complete automated test suite currently passes successfully.

* Tests: 52
* Failures: 0
* Errors: 0
* Exit code: 0

The automated tests cover authentication/security behavior, users, projects, project members, tasks, validation, and RBAC behavior.

Temporary automated test data uses `AUTOTEST-*` naming where applicable.

## Current Verification

The application has also been verified through Swagger UI.

Example:

`GET /api/projects/1`

returns HTTP `200 OK`.

## Security Notes

* Do not store database passwords in source files.
* Do not store JWT secrets in source files.
* Do not commit real JWT tokens.
* JWT tokens used during testing should be treated as temporary credentials.
* Rotate the JWT secret if a real secret has been exposed.

## Status

OpsFlow backend implementation and automated testing are complete.

The project is ready for final documentation, cleanup, and packaging.
