# Backend

Java 21 Spring Boot 3.5.16 API using Maven, Spring Security, Spring Data JPA/Hibernate, JWT support, BCrypt, Jakarta Validation, PostgreSQL, Liquibase, and OpenAPI.

Block 2 implements JWT login, active-account checks, role-protected admin user management, Liquibase identity tables, password reset/change, and first-admin bootstrap. Block 3 adds shared organisation/contact APIs. Blocks 4–5 add BDR prospecting and SQL qualification, organization-wide Sales Manager review, AE assignment/reassignment, history, notifications, and performance. Block 6 adds the AE L1/L2/L3 opportunity lifecycle, meetings, stakeholders, close/reopen history, and handovers. Block 8 adds Leadership analytics. Changeset 006 adds the AE lifecycle schema; restart the backend to apply pending migrations. Block 7 revival and target management remain unimplemented. Agreed defaults are recorded in [`../docs/OPEN-QUESTIONS.md`](../docs/OPEN-QUESTIONS.md).

Configuration reads database connection details from `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`. Hibernate schema auto-update is disabled; Liquibase owns schema changes. Set `APP_JWT_SECRET_BASE64` to a base64-encoded random secret of at least 32 bytes. On first launch only, set `APP_BOOTSTRAP_ADMIN_EMAIL` and `APP_BOOTSTRAP_ADMIN_PASSWORD` (at least 12 characters); the user must change it at first sign-in. Passwords are limited to 72 UTF-8 bytes for BCrypt. Do not commit secrets. `VITE_API_BASE_URL` defaults to `http://localhost:8080/api/v1`. Access JWTs expire after 15 minutes and are held in React memory only.

## First local setup (macOS)

1. Install [Postgres.app](https://postgresapp.com/downloads.html) by dragging it into Applications, open it, and click **Initialize**. The app includes PostgreSQL and the `psql` command-line tools. The official [install guide](https://postgresapp.com/documentation/install.html) has screenshots and details.
2. In Terminal, add Postgres.app's command-line tools to this Terminal session and connect to PostgreSQL:

   ```sh
   export PATH="/Applications/Postgres.app/Contents/Versions/latest/bin:$PATH"
   psql postgres
   ```

3. At the `psql` prompt, create the project database account and database. `\\password` asks for the password without putting it into shell history:

   ```text
   CREATE ROLE prosperr WITH LOGIN;
   \\password prosperr
   CREATE DATABASE prosperr_crm OWNER prosperr;
   \\q
   ```

4. From the project root, start the API's first-run setup:

   ```sh
   cd /Users/adityaagarwal/project1/backend
   bash scripts/first-run.sh
   ```

   It asks for the database password and first administrator name, work email, and temporary password. Password prompts are hidden. It generates a JWT signing secret in memory, applies the Liquibase migration, and creates the first ADMIN account with a required password change at first sign-in. The bootstrap credentials are not saved in the project.

5. In another Terminal window, start the React app:

   ```sh
   cd /Users/adityaagarwal/project1/frontend
   npm run dev
   ```

   Open the local Vite address printed in that terminal and sign in using the administrator email and temporary password you entered. After the first sign-in, choose a permanent password.

The first-run script requires `psql`, `mvn`, and `openssl` on `PATH`. Postgres.app installs into Applications and its official [command-line tools guide](https://postgresapp.com/documentation/cli-tools.html) explains how to add `psql` to `PATH` permanently if desired. This script is for the initial local setup; Spring Boot environment variables can also be set directly for later runs.

## Build

From this directory, run:

```sh
mvn -Dmaven.repo.local=/Users/adityaagarwal/.local/m2 test
```

Maven 3.10.1 is installed in the user-local tools folder. The explicit local repository path avoids modifying the existing default Maven cache location.
