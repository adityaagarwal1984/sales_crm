# UI/UX Guidelines and Design System

**Status:** Block 1 foundation implemented; product workflows and business rules remain placeholders.

## Design direction

- Enterprise SaaS CRM with a light theme, neutral slate background, white content surfaces, subtle borders, restrained shadows, and blue as the primary action accent.
- System font stack with consistent sizes and readable line heights. Avoid a remote font dependency in the local foundation.
- Use compact cards, clear hierarchy, meaningful data visualization, and short transitions. Avoid gradients, oversized panels, and decorative motion.
- Dashboard records and metrics are clearly labeled as illustrative. Leadership and AE money values use INR formatting only as a visual example; Prosperr's reporting currency and calculation rules remain unconfirmed.

## Tokens and styling

- Tailwind CSS 4 is configured through the Vite plugin and `src/index.css`.
- Core palette: slate neutrals (`slate-50`/`slate-100` backgrounds, `slate-200` borders, `slate-500` secondary text, `slate-900` primary text) and blue (`blue-600` / `blue-700`) for primary actions and active navigation.
- Success, warning, error, and neutral states use emerald, amber, rose, and slate with readable text labels; color is never the only status signal.
- Use rounded-md controls, rounded-xl content surfaces, a restrained 1px border, and shared `shadow-card` / `shadow-popover` values.
- Use `cn()` from `src/lib/utils.js` to merge conditional Tailwind classes. Do not duplicate long shared class strings across pages.
- Use Lucide React icons with consistent 16–20px sizing. Decorative icons are hidden from assistive technology.

## Shared application shell

- `AppShell` owns the persistent sidebar, top navigation, main content width, and footer.
- The sidebar contains Prosperr workspace branding, current role, role-specific navigation, and a preview status note.
- The top bar contains search scoped to the current dashboard's preview rows, notifications, and a demo profile menu. The profile menu switches between the five dashboard routes; it is not authentication. On small screens the same search appears in the dashboard toolbar.
- `PageHeader` supplies breadcrumb, title, description, and optional action placement.
- Use a fixed-width sidebar on large screens. On small screens, open it as an overlay from the menu button. Main content uses a fluid width capped at 1440px.
- Only the dashboard route is active for each role in this block. Other entries are visibly marked “Soon” and disabled until their feature blocks are implemented.

## Role navigation

Navigation is data-driven in `src/config/roleNavigation.js`, keyed by `ADMIN`, `BDR`, `SALES_MANAGER`, `AE`, and `LEADERSHIP`. Add future routes and menu items in this configuration instead of branching across unrelated sidebar components. Do not imply that a disabled future area already works.

## Component conventions

Reusable components live in `src/components/crm/` or `src/components/ui/`.

| Component | Convention |
|---|---|
| `StatCard` | Label, prominent value, concise context, and semantic Lucide icon; no unlabeled color-only meaning |
| `DataTable` | Semantic table headers, responsive horizontal overflow, concise secondary detail, explicit empty state, and pagination |
| `SearchInput` | Visible placeholder and accessible label; clear affordance appears only when a query exists |
| `FilterDropdown` | Labeled single-select, default “All statuses,” and options derived from the displayed set |
| `StatusBadge` | Human-readable status text plus a consistent semantic tone |
| `Modal` | Radix Dialog primitive, clear title/description, explicit cancel/submit actions, and no assumed persistence |
| `ConfirmationDialog` | Radix Alert Dialog; use only when a future confirmed action needs deliberate confirmation |
| `FormField` | Label bound to control ID; hint and validation error are associated with `aria-describedby` |
| `Pagination` | Explicit previous/next labels, disabled boundaries, current page, and result count |
| `EmptyState` | Explains the empty result and the next safe action, such as adjusting search/filter |
| `LoadingSkeleton` | `role="status"` and a descriptive label; match the shape of the content being loaded |
| `PageHeader` | Breadcrumb, one page title, short description, optional action |
| `ChartContainer` | Title/description, responsive chart, readable axes, legend, tooltip, and text alternative |

The frontend is JavaScript-only. `components.json` configures shadcn/ui conventions (`tsx: false`, `@/components/ui`, and Lucide); `jsconfig.json` and Vite share the `@/*` source alias. Source-owned primitives use shadcn-style composition with Radix UI for accessible dropdowns and dialogs; extend these local components instead of adding one-off copies. Prefer small explicit props and keep business rules out of shared presentation components.

For authentication, use a focused sign-in form with visible field labels and a generic credential error. Keep access tokens in React memory. For account administration, show name, email, role, active status, and explicit edit/activate/reset actions; never display credential data. Destructive access changes should explain the outcome and preserve at least one active administrator.

## Data and interaction boundaries

- `src/data/dashboardData.js` contains static illustrative preview content only. It is not the CRM source of truth and must not be used as production data.
- Dashboard search, status filter, and pagination operate only on the local preview rows.
- Authentication, login, password change, and administrator user actions use backend APIs. Other role dashboards, notifications, date range, and business actions remain presentation placeholders until their blocks are built.
- Replace sample metrics only after metric definitions, currency, and workflow states are confirmed.

## Accessibility and responsive behavior

- Use semantic `header`, `nav`, `main`, `footer`, headings, table headers, labels, and buttons.
- Keep visible keyboard focus. Dropdowns and dialogs use Radix keyboard and focus behavior.
- Provide accessible labels for icon-only controls and name status in text.
- Use Tailwind responsive breakpoints to keep dashboard cards readable, collapse navigation on mobile, and allow wide tables to scroll horizontally.
- WCAG 2.2 AA is the proposed target from the initial guide; final support and acceptance criteria still require stakeholder confirmation.

## Local checks

- `npm run dev` — run the Vite preview.
- `npm run build` — ensure the UI compiles for production.
- `npm test` — verify authentication routing and forced password change.

## Decisions for later review

Brand assets/colors, product-approved currency, dashboard metric definitions, supported viewport/browser matrix, localization, dark mode, table density, notification behavior, and eventual API loading/error states remain open.
