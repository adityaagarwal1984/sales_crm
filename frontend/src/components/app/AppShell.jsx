import { useEffect, useState } from "react";
import { Outlet, useLocation } from "react-router-dom";
import { Sidebar } from "@/components/app/Sidebar";
import { TopBar } from "@/components/app/TopBar";
import { roleNavigation } from "@/config/roleNavigation";
import { useAuth } from "@/auth/AuthContext";

export function AppShell() {
  const [mobileNavOpen, setMobileNavOpen] = useState(false);
  const [search, setSearch] = useState("");
  const location = useLocation();
  const { user } = useAuth();
  const role = user?.role ?? "ADMIN";

  useEffect(() => {
    setSearch("");
  }, [location.pathname]);

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 lg:flex">
      <Sidebar role={role} open={mobileNavOpen} onClose={() => setMobileNavOpen(false)} />
      <div className="flex min-h-screen min-w-0 flex-1 flex-col">
        <TopBar role={role} search={search} onSearchChange={setSearch} onMenuClick={() => setMobileNavOpen(true)} />
        <main className="flex-1 px-4 py-6 sm:px-6 sm:py-8 lg:px-8">
          <div className="mx-auto w-full max-w-[1440px]"><Outlet context={{ search, setSearch }} /></div>
        </main>
        <footer className="border-t border-slate-200 bg-white px-4 py-3 text-center text-[11px] text-slate-400 sm:px-6 lg:px-8">
          {role === "ADMIN" ? "Prosperr Sales CRM · Administrator workspace" : role === "BDR" ? "Prosperr Sales CRM · Business Development workspace" : role === "SALES_MANAGER" ? "Prosperr Sales CRM · Sales Manager workspace" : role === "AE" ? "Prosperr Sales CRM · Account Executive workspace" : "Prosperr Sales CRM · Leadership workspace"}
        </footer>
      </div>
    </div>
  );
}
