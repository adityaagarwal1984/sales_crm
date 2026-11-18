# ADR-0005: Keep Organisations and Contacts in a Separate CRM Capability

- **Status:** Implemented with provisional business defaults; tech-lead confirmation remains open
- **Date:** 2026-11-19

## Context

Block 3 introduces prospect organisations and people at those organisations. The brief explicitly distinguishes a contact (a person) from a lead/sales pursuit, but does not define record visibility, controlled vocabularies, or complete duplicate rules.

## Decision

- Add a feature-scoped `organisation` module using controller → service → repository → entity boundaries, DTOs, Liquibase, and PostgreSQL foreign keys.
- Keep contact as a separate entity with a required many-to-one organisation relationship. Do not represent a contact as a lead or pursuit.
- Allow every CRM role to read the shared directory; BDR and ADMIN can create and edit organisations and contacts. Store the authenticated creator on organisations for traceability; contact-level creator attribution may be added if a reporting requirement needs it.
- Use free-text industry, company size, and lead source until Prosperr supplies canonical values.
- Reject organisation names that match after trim and case folding. Reject a repeated non-empty contact email within the same organisation, but allow it for a different organisation. Enforce both policies in the database as well as the service.
- Include an optimistic-lock version on organisation and contact rows to avoid silently overwriting concurrent edits.
- Use Spring Data page/size responses, defaulting to creation date descending and limiting page size to 100.

## Alternatives considered

- Restrict all reads/writes to the creator: deferred because BDR collaboration was not defined and the role brief describes a shared discovery function. Revisit if ownership scope is required.
- Globally unique contact email: rejected because a person may work with multiple organisations or change employers.
- Enumerated industry/company-size/source values: deferred because no allowed values were supplied.
- Add a generic Specification framework: rejected for the current fixed filter set; explicit repository query predicates are simpler.
- Add custom factories, strategies, or domain events: rejected because this block has no creation variants, changing policies, or independent event consumers.

## Consequences

- The module is searchable, paginated, secured server-side, and ready to connect to later SQL/opportunity workflows through the organisation/contact IDs.
- Duplicate names are deliberately strict; legitimate separate legal entities with the same normalized name may need a future override or a different business key.
- Search uses fixed query parameters and does not yet include advanced sorting, ownership/team filtering, audit history, delete/archive, or imports.
- The access and duplicate policies must be reviewed by the tech lead before production use.
