# Design principles

Apply principles where they make a change easier to understand, test, or safely extend. Do not add abstractions solely to demonstrate a principle.

| Principle | Meaning | CRM example | Avoids |
|---|---|---|---|
| **SRP** | One main reason for a unit to change. | Controllers handle HTTP; services apply workflow rules; repositories query. | God classes and transport code containing business rules. |
| **OCP** | Extend through a seam when genuine variants exist. | Role navigation is configuration-driven; new role still requires deliberate backend authorization updates. | Copying role conditionals into every page; speculative plugin systems. |
| **LSP** | Subtypes must honor their parent contract. | No role-specific `UserEntity` subclasses; role is a relation/enum. | Inheritance that weakens validation or access guarantees. |
| **ISP** | Consumers depend only on needed operations. | Use focused Spring Data repositories; avoid broad custom service interfaces without multiple consumers. | Fat interfaces and unused abstractions. |
| **DIP** | Business policy receives dependencies through stable boundaries. | Constructor-injected services depend on repositories, encoders, and JWT service. | Hidden globals and business code tied to direct database calls. |
| **DRY** | Keep each rule authoritative in its boundary. | Central email normalization and shared frontend API client; DB unique index is final duplicate guard. | Validation and security rules drifting across copies. |
| **KISS** | Choose the simplest design satisfying known requirements. | One role per account; explicit opportunity transition checks. | Premature multi-role framework or workflow engine. |
| **Separation of concerns** | Keep UI, HTTP, policy, and persistence apart. | React pages → API client; Spring controller → service → repository. | Treating UI route guards as security or leaking entities into APIs. |
| **Encapsulation** | State changes preserve their invariants. | Entity operations for password changes and opportunity transitions. | Partial updates that leave coupled fields inconsistent. |
| **High cohesion / low coupling** | Group related behavior and minimize cross-module knowledge. | Packages follow identity, directory, prospecting, sales, analytics capabilities. | Global utility/service dumping grounds. |
| **Composition over inheritance** | Assemble behavior from collaborators/components. | `AppShell` composes sidebar, top bar, and pages; user references a role. | Brittle base classes and role-specific subclasses without behavior. |

### Application checklist

Before a feature, state its rule owner (frontend, service, or database), enforce authorization in the backend, keep DTOs separate from entities, and use transactions/constraints for cross-record consistency. Add a new interface, pattern, or module only when there is real variation, a second implementation, or an independent responsibility. Current improvement candidates include moving Admin dashboard counts from `AdminUserController` into a query/service boundary and paginating Admin user lists; these are documented observations, not changes made by this guide.

See [Interview Guide](INTERVIEW-GUIDE.md) for specific implementations and [Design Patterns](DESIGN-PATTERNS.md) for selected patterns and deliberate non-use.
