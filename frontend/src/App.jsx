import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { AppShell } from "@/components/app/AppShell";
import { DashboardPage } from "@/components/dashboard/DashboardPage";
import { roleNavigation, roleOrder } from "@/config/roleNavigation";
import { AuthProvider, useAuth } from "@/auth/AuthContext";
import { LoginPage } from "@/pages/LoginPage";
import { ChangePasswordPage } from "@/pages/ChangePasswordPage";
import { UserManagementPage } from "@/pages/UserManagementPage";
import { AdminDashboardPage } from "@/pages/AdminDashboardPage";
import { OrganisationListPage } from "@/pages/organisation/OrganisationListPage";
import { OrganisationDetailPage } from "@/pages/organisation/OrganisationDetailPage";
import { ContactListPage } from "@/pages/organisation/ContactListPage";
import { ContactDetailPage } from "@/pages/organisation/ContactDetailPage";
import { BdrDashboardPage, LeadListPage, LeadDetailPage, CallsPage, FollowUpsPage, SqlSubmissionsPage } from "@/pages/bdr/BdrPages";
import { SalesManagerSqlQueuePage } from "@/pages/bdr/SalesManagerSqlQueuePage";
import { SqlQualificationFormPage } from "@/pages/bdr/SqlQualificationFormPage";
import { SalesManagerSqlReviewPage } from "@/pages/bdr/SalesManagerSqlQueuePage";

function Guard({children}) { const {user,loading}=useAuth(); const location=useLocation(); if(loading)return null; if(!user)return <Navigate to="/login" replace state={{from:location.pathname}}/>; if(user.mustChangePassword&&location.pathname!=="/change-password")return <Navigate to="/change-password" replace/>; const wanted=Object.entries(roleNavigation).find(([,v])=>location.pathname===`/${v.dashboard}`)?.[0]; if(wanted&&wanted!==user.role)return <Navigate to={`/${roleNavigation[user.role]?.dashboard||"admin"}`} replace/>; if(location.pathname.startsWith("/admin")&&user.role!=="ADMIN")return <Navigate to={`/${roleNavigation[user.role]?.dashboard||"admin"}`} replace/>; return children; }
function BdrOnly({children}) { const {user}=useAuth(); return user?.role==="BDR"?children:<Navigate to={`/${roleNavigation[user?.role]?.dashboard||"admin"}`} replace/>; }
function SalesManagerOnly({children}) { const {user}=useAuth(); return user?.role==="SALES_MANAGER"?children:<Navigate to={`/${roleNavigation[user?.role]?.dashboard||"admin"}`} replace/>; }

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage/>}/>
      <Route path="/change-password" element={<Guard><ChangePasswordPage/></Guard>}/>
      <Route path="/" element={<Navigate to="/admin" replace />} />
      <Route element={<Guard><AppShell /></Guard>}>
        {roleOrder.map((role) => (
          <Route key={role} path={`/${roleNavigation[role].dashboard}`} element={role === "ADMIN" ? <AdminDashboardPage/> : role === "BDR" ? <BdrDashboardPage/> : <DashboardPage role={role} />} />
        ))}
        <Route path="/admin/users" element={<UserManagementPage/>}/>
        <Route path="/organisations" element={<OrganisationListPage/>}/>
        <Route path="/organisations/:id" element={<OrganisationDetailPage/>}/>
        <Route path="/contacts" element={<ContactListPage/>}/>
        <Route path="/contacts/:id" element={<ContactDetailPage/>}/>
        <Route path="/bdr/leads" element={<BdrOnly><LeadListPage/></BdrOnly>}/>
        <Route path="/bdr/leads/:id" element={<BdrOnly><LeadDetailPage/></BdrOnly>}/>
        <Route path="/bdr/calls" element={<BdrOnly><CallsPage/></BdrOnly>}/>
        <Route path="/bdr/follow-ups" element={<BdrOnly><FollowUpsPage/></BdrOnly>}/>
        <Route path="/bdr/sql-qualifications" element={<BdrOnly><SqlSubmissionsPage/></BdrOnly>}/>
        <Route path="/bdr/leads/:id/qualification" element={<BdrOnly><SqlQualificationFormPage/></BdrOnly>}/>
        <Route path="/sales-manager/sql-qualifications" element={<SalesManagerOnly><SalesManagerSqlQueuePage/></SalesManagerOnly>}/>
        <Route path="/sales-manager/sql-qualifications/:id" element={<SalesManagerOnly><SalesManagerSqlReviewPage/></SalesManagerOnly>}/>
      </Route>
      <Route path="*" element={<Navigate to="/admin" replace />} />
    </Routes>
  );
}

export default function App() {
  return <AuthProvider><AppRoutes /></AuthProvider>;
}
