# ADR-0004: Add Design Patterns Only for Demonstrated Needs

- **Status:** Proposed for tech-lead review before Block 3
- **Date:** 2026-11-19

## Context

The project is at an early stage with a small identity capability and unresolved CRM workflow definitions. Pattern use can improve consistency when a genuine recurring variation exists, but speculative abstractions would slow an internship-sized modular monolith and encode unconfirmed business assumptions.

## Decision

For each upcoming development block, identify the concrete problem, first consider the direct/simple design, then select a pattern only if it solves recurring complexity or establishes a real boundary (for example, an Adapter for a selected external provider or State/transition policy for a validated complex lifecycle). Record the selected pattern or explicitly record why no pattern was needed in `docs/PATTERN-IMPLEMENTATION-LOG.md`.

Do not add interfaces for every class, custom factories for single object creation, generic workflow engines, microservices, or event infrastructure without a confirmed need.

## Alternatives considered

- Apply a pattern to every named design-pattern category: rejected because pattern presence is not a quality metric.
- Let every feature choose abstractions ad hoc without a record: rejected because it obscures cross-module conventions.
- Adopt a lightweight decision-and-log rule: selected because it creates consistency while preserving simple implementations.

## Consequences

- Each feature proposal explains principles, pattern need, and alternatives before implementation.
- New patterns require documenting concrete classes, examples, benefits, and trade-offs.
- The guidance can be amended when a real business workflow or integration demonstrates a new pressure.
