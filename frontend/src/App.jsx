import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { AppShell } from "@/components/app/AppShell";
import { DashboardPage } from "@/components/dashboard/DashboardPage";
import { roleNavigation, roleOrder } from "@/config/roleNavigation";
import { AuthProvider, useAuth } from "@/auth/AuthContext";
import { LoginPage } from "@/pages/LoginPage";
import { ChangePasswordPage } from "@/pages/ChangePasswordPage";
import { UserManagementPage } from "@/pages/UserManagementPage";
import { AdminDashboardPage } from "@/pages/AdminDashboardPage";

function Guard({children}) { const {user,loading}=useAuth(); const location=useLocation(); if(loading)return null; if(!user)return <Navigate to="/login" replace state={{from:location.pathname}}/>; if(user.mustChangePassword&&location.pathname!=="/change-password")return <Navigate to="/change-password" replace/>; const wanted=Object.entries(roleNavigation).find(([,v])=>location.pathname===`/${v.dashboard}`)?.[0]; if(wanted&&wanted!==user.role)return <Navigate to={`/${roleNavigation[user.role]?.dashboard||"admin"}`} replace/>; if(location.pathname.startsWith("/admin")&&user.role!=="ADMIN")return <Navigate to={`/${roleNavigation[user.role]?.dashboard||"admin"}`} replace/>; return children; }

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage/>}/>
      <Route path="/change-password" element={<Guard><ChangePasswordPage/></Guard>}/>
      <Route path="/" element={<Navigate to="/admin" replace />} />
      <Route element={<Guard><AppShell /></Guard>}>
        {roleOrder.map((role) => (
          <Route key={role} path={`/${roleNavigation[role].dashboard}`} element={role === "ADMIN" ? <AdminDashboardPage/> : <DashboardPage role={role} />} />
        ))}
        <Route path="/admin/users" element={<UserManagementPage/>}/>
      </Route>
      <Route path="*" element={<Navigate to="/admin" replace />} />
    </Routes>
  );
}

export default function App() {
  return <AuthProvider><AppRoutes /></AuthProvider>;
}
