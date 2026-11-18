import { Bell, ChevronDown, Menu, Settings2 } from "lucide-react";
import { Link } from "react-router-dom";
import { roleLabels, roleNavigation } from "@/config/roleNavigation";
import { useAuth } from "@/auth/AuthContext";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { SearchInput } from "@/components/crm/SearchInput";
import {
  DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel,
  DropdownMenuSeparator, DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

export function TopBar({ role, search, onSearchChange, onMenuClick }) {
  const { user, logout } = useAuth();
  const current = roleNavigation[role];
  const initial = roleLabels[role].charAt(0);

  return (
    <header className="sticky top-0 z-30 flex h-[72px] items-center justify-between gap-4 border-b border-slate-200 bg-white/95 px-4 backdrop-blur sm:px-6 lg:px-8">
      <div className="flex min-w-0 items-center gap-3">
        <Button variant="ghost" size="icon" className="lg:hidden" aria-label="Open navigation" onClick={onMenuClick}><Menu className="size-5" /></Button>
        <div className="hidden md:block">
          <SearchInput value={search} onChange={onSearchChange} placeholder="Search current dashboard…" label="Search current dashboard records" className="w-[min(360px,35vw)]" />
        </div>
      </div>

      <div className="flex shrink-0 items-center gap-1.5 sm:gap-2">
        <Badge tone="neutral" className="hidden sm:inline-flex">{role === "ADMIN" ? "Admin" : "Preview"}</Badge>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon" aria-label="Notifications" className="relative text-slate-500">
              <Bell className="size-[18px]" />
              <span className="absolute right-2 top-2 size-1.5 rounded-full bg-blue-600 ring-2 ring-white" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-80">
            <DropdownMenuLabel className="flex items-center justify-between">Notifications <span className="text-[10px] font-normal text-slate-400">Illustrative</span></DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem className="items-start"><span className="mt-1.5 size-2 shrink-0 rounded-full bg-blue-500" /><span><span className="block font-medium text-slate-800">3 SQLs need review</span><span className="mt-0.5 block text-xs text-slate-500">A sample notification for this dashboard preview.</span></span></DropdownMenuItem>
            <DropdownMenuItem className="items-start"><span className="mt-1.5 size-2 shrink-0 rounded-full bg-amber-400" /><span><span className="block font-medium text-slate-800">Follow-up due today</span><span className="mt-0.5 block text-xs text-slate-500">Sample reminder · 10 minutes ago</span></span></DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem disabled className="justify-center text-xs text-slate-400">Notification actions are not connected yet</DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="h-10 gap-2 px-1.5 sm:px-2" aria-label="Open profile menu">
              <span className="grid size-8 place-items-center rounded-full bg-blue-100 text-xs font-semibold text-blue-700">{(user?.fullName||initial).charAt(0)}</span>
              <span className="hidden text-left sm:block"><span className="block text-xs font-medium text-slate-800">{user?.fullName}</span><span className="block text-[10px] text-slate-500">{roleLabels[user?.role||role]}</span></span>
              <ChevronDown className="hidden size-3.5 text-slate-400 sm:block" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-60">
            <DropdownMenuLabel className="font-normal"><span className="block font-medium text-slate-800">{user?.fullName}</span><span className="mt-0.5 block text-xs text-slate-500">{user?.email}</span></DropdownMenuLabel>
            {user?.role === "ADMIN" && <DropdownMenuItem asChild><Link to="/admin/users">Users & roles</Link></DropdownMenuItem>}
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={logout}>Sign out</DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}
