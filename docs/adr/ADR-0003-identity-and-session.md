# ADR-0003: Local Identity and In-Memory Access JWT

- **Status:** Accepted for Block 2
- **Date:** 2026-11-18

## Context

Block 2 requires email/password login, role-based access, deactivated-account rejection, and an administrator-only user lifecycle. The confirmed defaults are one role per user, a first administrator provisioned during startup, and a mandatory password change after a temporary password.

## Decision

- Hash passwords with BCrypt; do not return password hashes in API DTOs.
- Return a signed 15-minute access JWT from login and keep it in React memory only. There is no refresh token in this MVP.
- Re-read the current active user for each authenticated API request so deactivation takes effect without waiting for token expiry.
- Store one `RoleCode` relationship on a user; the server remains the authorization authority.
- Create the first administrator only when no ADMIN account exists and the operator supplies bootstrap values through environment variables. No public admin-signup endpoint exists.
- Admin-created/reset accounts use a temporary password and must change it after authentication.

## Consequences

- A page refresh loses the in-memory token and requires another login; this is the selected simple session behavior.
- Each authenticated request performs a user lookup; this supports immediate deactivation but adds a database read.
- Role additions require aligned server and frontend configuration; backend policy is authoritative.
- Bootstrap secrets must be provided securely at first startup and must not be committed.
- A production identity provider, refresh flow, audit requirements, and rate limiting remain future decisions.
