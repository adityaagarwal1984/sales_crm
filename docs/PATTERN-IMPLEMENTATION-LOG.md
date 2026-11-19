# Pattern implementation log

Only record patterns/techniques present in code. See [Design Patterns](DESIGN-PATTERNS.md) for candidates deliberately not used.

| Pattern / technique | Example in this CRM | Reason and trade-off |
|---|---|---|
| Modular monolith | `com.prosperr.crm` capability packages within one Spring Boot app | Keeps deployment/transactions simple; requires package ownership discipline. |
| MVC + Service Layer + Repository | Feature controllers delegate to services and Spring Data repositories | Separates HTTP, rules, and storage; DTO/mapping work remains. |
| DTO | Identity, directory, prospecting, sales API request/response records | Controls contract and secret exposure; mapping is explicit. |
| Dependency Injection | Constructor-injected Spring services/controllers | Makes collaborators clear; avoid needless interfaces. |
| Security filter chain | `JwtAuthenticationFilter` in Spring Security | Reuses standard request pipeline; ordering/configuration requires care. |
| Builder (library use) | `JwtClaimsSet.builder()` in `JwtService` | Clear claim construction; no custom builder abstraction exists. |
| Explicit state transitions | SQL qualification status and opportunity stage enum/service checks | Current workflows are small; custom State classes would add indirection. |
| Append-only history | Qualification, assignment, stage, closure, reopen records | Preserves audit trail; current state plus history requires transactional updates. |
| Analytics read model | Separate SQL aggregates in Leadership analytics service | Avoids ORM-heavy dashboard loading and join fan-out; formulas must be explicit. |

## Feature decisions

- **Organisation/contacts (Block 3):** reused the established MVC/service/repository/DTO approach. No factory, strategy, specification, or event bus: one create flow, fixed search filters, and no independent event consumers. Contact counts for the current result page are grouped in one query.
- **BDR calls and follow-ups (Block 4):** simple service orchestration plus immutable call rows. Completing follow-up creates a new call; no State/Observer abstraction for the current fixed outcomes.
- **SQL qualification (Block 4):** separate qualification record, enum status, transactional review, unique lead relationship, and append-only history. Avoided mixing SQL into lead or adding a generic workflow engine.
- **Manager assignment (Block 5):** assignment transaction updates current owner, writes history, and creates notification. No domain event/outbox while writes are synchronous in one application/database.
- **AE lifecycle (Block 6):** explicit transition checks and stage/closure history. No custom State classes for five known outcomes; reopening is a separately authorized manager decision.
- **Leadership analytics (Block 8):** SQL aggregates and separate counts protect KPI correctness from one-to-many join multiplication. No cache/materialized view without measured need.

For a new feature, add one row naming the real class/file, problem, selected pattern or simpler design, alternative, and trade-off. Full architectural decisions belong in [`adr/`](adr/).
