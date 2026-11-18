import { Link, useLocation } from "react-router-dom";
import { BadgeCheck, CircleHelp, X } from "lucide-react";
import { roleLabels, roleNavigation } from "@/config/roleNavigation";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";

export function Sidebar({ role, open = false, onClose }) {
  const location = useLocation();
  const config = roleNavigation[role];
  const dashboardPath = `/${config.dashboard}`;

  return (
    <>
      {open ? <button aria-label="Close navigation" className="fixed inset-0 z-40 bg-slate-950/30 lg:hidden" onClick={onClose} /> : null}
      <aside className={cn(
        "fixed inset-y-0 left-0 z-50 flex w-[264px] flex-col border-r border-slate-200 bg-white transition-transform lg:static lg:z-auto lg:translate-x-0",
        open ? "translate-x-0" : "-translate-x-full lg:translate-x-0",
      )}>
        <div className="flex h-[72px] items-center justify-between px-5">
          <Link to={dashboardPath} onClick={onClose} className="flex items-center gap-3 rounded-md focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500">
            <span className="grid size-9 place-items-center rounded-xl bg-blue-600 text-sm font-bold text-white shadow-sm">P</span>
            <span><span className="block text-[15px] font-semibold tracking-tight text-slate-950">Prosperr</span><span className="block text-[10px] font-medium uppercase tracking-[.15em] text-slate-400">Sales workspace</span></span>
          </Link>
          <Button variant="ghost" size="icon" className="lg:hidden" aria-label="Close navigation" onClick={onClose}><X className="size-4" /></Button>
        </div>

        <div className="px-4 pb-4">
          <div className="rounded-lg border border-slate-200 bg-slate-50/70 px-3 py-2.5">
            <p className="text-[10px] font-semibold uppercase tracking-[.12em] text-slate-400">Current workspace</p>
            <p className="mt-1 text-sm font-medium text-slate-800">{roleLabels[role]}</p>
          </div>
        </div>

        <Separator />
        <nav aria-label="Main navigation" className="flex-1 space-y-1 overflow-y-auto px-3 py-5">
          <p className="mb-2 px-3 text-[10px] font-semibold uppercase tracking-[.14em] text-slate-400">Workspace</p>
          {config.items.map((item) => {
            const Icon = item.icon;
            if (item.available) {
              const target = item.href || dashboardPath;
              const active = location.pathname === target || (item.href && location.pathname.startsWith(`${target}/`));
              return (
                <Link
                  key={item.label}
                  to={target}
                  onClick={onClose}
                  aria-current={active ? "page" : undefined}
                  className={cn("flex h-10 items-center gap-3 rounded-md px-3 text-sm transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500", active ? "bg-blue-50 font-medium text-blue-700" : "text-slate-600 hover:bg-slate-50 hover:text-slate-900")}
                >
                  <Icon className="size-4 shrink-0" aria-hidden="true" />{item.label}
                </Link>
              );
            }
            return (
              <button key={item.label} type="button" disabled title={`${item.label} will be added in a later development block`} className="flex h-10 w-full cursor-not-allowed items-center gap-3 rounded-md px-3 text-sm text-slate-400">
                <Icon className="size-4 shrink-0" aria-hidden="true" />{item.label}
                <span className="ml-auto text-[9px] font-medium uppercase tracking-wide text-slate-300">Soon</span>
              </button>
            );
          })}
        </nav>

        <div className="space-y-3 border-t border-slate-100 p-4">
          <div className="flex items-center gap-2 rounded-lg bg-slate-50 px-3 py-2.5">
            <BadgeCheck className="size-4 text-emerald-600" aria-hidden="true" />
            <div><p className="text-xs font-medium text-slate-700">{role === "ADMIN" ? "Account secured" : "Design preview"}</p><p className="text-[10px] text-slate-500">{role === "ADMIN" ? "Role-based access enabled" : "Illustrative data only"}</p></div>
          </div>
          <button type="button" className="flex items-center gap-2 px-2 text-xs text-slate-500 hover:text-slate-800"><CircleHelp className="size-4" /> Help & feedback</button>
        </div>
      </aside>
    </>
  );
}
