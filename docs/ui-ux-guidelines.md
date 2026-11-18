# UI/UX Guidelines

**Status: Initial design principles from the supplied brief.** Final visual tokens and user flows should be reviewed with Prosperr stakeholders.

## Shared product shell

- One React application and one shared shell: role-aware sidebar, top navigation, page title/breadcrumbs, content area, and consistent responsive behavior.
- Role-specific dashboards and navigation live within that shared design system; avoid five separate applications.
- Use reusable components for tables, forms, filters, status badges, cards, dialogs, and feedback states.

## Interaction requirements

- Every data view has loading, empty, error, and populated states.
- Tables support agreed search, filtering, sorting, and pagination; preserve useful context when returning from detail views.
- Forms provide clear labels, validation messages, save/cancel behavior, and success/error feedback.
- Use confirmation for destructive or irreversible actions once such actions are defined.
- Status should be communicated with readable text as well as color.
- Charts need clear labels, time ranges, definitions, and accessible alternatives; no misleading truncated axes.

## Visual direction

Professional SaaS CRM: consistent spacing, typography, color, and density. Use Tailwind CSS and shadcn/ui as the implementation toolkit, with Lucide React icons and Recharts where a chart helps a user make a decision.

## Accessibility and responsive behavior

Target WCAG 2.2 AA is proposed, pending product acceptance. Keyboard operation, visible focus, semantic controls, sufficient contrast, and accessible form errors should be part of component acceptance. Define smallest supported viewport and supported browsers before release.

## Design questions

Brand assets/colors, dark mode, density preferences, dashboard customization, table export, localization, date/time display, and analytics definitions remain open.
