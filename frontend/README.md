# Frontend

Planned React + Vite + JavaScript application. Tailwind CSS, shadcn/ui, React Router, Axios, Recharts, and Lucide React are requested in the brief.

The Block 1 interface foundation and Block 3 organisation/contact screens are implemented. Organisation and contact data is loaded from the Spring Boot API; other business dashboards remain placeholders. See [`../docs/07-UI-UX-GUIDELINES.md`](../docs/07-UI-UX-GUIDELINES.md), [`../docs/api-documentation.md`](../docs/api-documentation.md), and [`../docs/OPEN-QUESTIONS.md`](../docs/OPEN-QUESTIONS.md).

## Local development

Use Node.js 24 LTS and npm. From this folder:

```sh
npm install
npm run dev
```

The dev server prints the local URL. Verify the build with `npm run build` and role-route navigation with `npm test`.

Dashboard values are illustrative placeholders except for the implemented Admin user-management and BDR organisation/contact screens. Authentication uses the backend; no demo role switcher is present.
