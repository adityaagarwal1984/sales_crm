# UI conventions

## Visual system

- Light theme with Prosperr deep-green/mint accents, neutral page background, white surfaces, subtle borders, and restrained shadows.
- Use consistent spacing, typography, status colors, and responsive widths. Avoid decorative gradients, oversized cards, and motion that competes with task completion.
- The public landing page is at `/`; employee login remains `/login` and uses the existing authentication API.
- Every authenticated role uses the shared app shell, role-specific navigation, Prosperr mark, profile controls, and consistent page headers.

## Components and interaction

Use shared primitives in `frontend/src/components/` for forms, dialogs, status, tables, pagination, empty/loading/error states, and charts. Keep labels visible, associate errors with fields, use semantic HTML, support keyboard/focus, and provide text meaning in addition to color.

Tables should be searchable/filterable where relevant, paginated for large results, and horizontally usable on narrow screens. Confirm destructive or consequential actions. Show API loading, success, validation, and failure states.

## Navigation and authority

Role navigation is configured in `frontend/src/config/roleNavigation.js`. The frontend controls presentation and routing; Spring Security and service ownership checks enforce access. Shared organisation/contact directory data is visible according to backend permissions. Dashboard numbers and records should come from APIs, not illustrative preview data.

## Accessibility and responsive behavior

Use keyboard-operable controls, visible focus, descriptive icon labels, adequate contrast, responsive layout, and accessible chart descriptions. Confirm final WCAG target, browser matrix, and localization with the product owner; they are not established by this guide.
