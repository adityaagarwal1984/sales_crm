# ADR-0002: Use a Java 21 / Spring Boot 3.5 / Maven Local Baseline

- **Status:** Proposed local development baseline; confirm Prosperr's required versions.
- **Date:** 2026-11-18

## Context

The developer laptop has Java 21 installed. No backend project or prescribed Spring Boot/build version was present in the repository.

## Decision

Start the backend on Java 21, Spring Boot 3.5.16, and Maven 3.10.1. Java 21 is within Spring Boot 3.5's supported range. Use Maven to keep the project setup familiar and explicit for the internship.

## Consequences

- The checked-in Maven POM captures the build versions and dependency graph.
- Maven is installed under the developer's user-local tools directory, not system-wide.
- A tech-lead standard may require different versions; resolve that before production/deployment decisions.
- The local dependency cache uses `/Users/adityaagarwal/.local/m2` to avoid the existing default cache path.
