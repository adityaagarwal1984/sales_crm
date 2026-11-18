import { Navigate, Route, Routes } from "react-router-dom";
import { AppShell } from "@/components/app/AppShell";
import { DashboardPage } from "@/components/dashboard/DashboardPage";
import { roleNavigation, roleOrder } from "@/config/roleNavigation";

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/admin" replace />} />
      <Route element={<AppShell />}>
        {roleOrder.map((role) => (
          <Route key={role} path={`/${roleNavigation[role].dashboard}`} element={<DashboardPage role={role} />} />
        ))}
      </Route>
      <Route path="*" element={<Navigate to="/admin" replace />} />
    </Routes>
  );
}

export default function App() {
  return <AppRoutes />;
}
