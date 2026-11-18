# Backend

Java 21 Spring Boot 3.5.16 API using Maven, Spring Security, Spring Data JPA/Hibernate, JWT support, BCrypt, Jakarta Validation, PostgreSQL, Liquibase, and OpenAPI.

Block 2 implements JWT login, active-account checks, role-protected admin user management, Liquibase identity tables, password reset/change, and first-admin bootstrap. Agreed defaults are recorded in [`../docs/OPEN-QUESTIONS.md`](../docs/OPEN-QUESTIONS.md).

Configuration reads database connection details from `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`. Hibernate schema auto-update is disabled; Liquibase owns schema changes. Set `APP_JWT_SECRET_BASE64` to a base64-encoded random secret of at least 32 bytes. On first launch only, set `APP_BOOTSTRAP_ADMIN_EMAIL` and `APP_BOOTSTRAP_ADMIN_PASSWORD` (at least 12 characters); the user must change it at first sign-in. Passwords are limited to 72 UTF-8 bytes for BCrypt. Do not commit secrets. `VITE_API_BASE_URL` defaults to `http://localhost:8080/api/v1`. Access JWTs expire after 15 minutes and are held in React memory only.

## Build

From this directory, run:

```sh
mvn -Dmaven.repo.local=/Users/adityaagarwal/.local/m2 test
```

Maven 3.10.1 is installed in the user-local tools folder. The explicit local repository path avoids modifying the existing default Maven cache location.
