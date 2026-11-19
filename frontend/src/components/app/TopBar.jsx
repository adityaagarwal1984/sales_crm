import { Bell, ChevronDown, Menu } from "lucide-react";
import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { roleLabels, roleNavigation } from "@/config/roleNavigation";
import { useAuth } from "@/auth/AuthContext";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { SearchInput } from "@/components/crm/SearchInput";
import { api } from "@/lib/api";
import {
  DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel,
  DropdownMenuSeparator, DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

export function TopBar({ role, search, onSearchChange, onMenuClick }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [notificationList, setNotificationList] = useState({ items: [], unreadCount: 0 });
  const [notificationsOpen, setNotificationsOpen] = useState(false);
  const initial = roleLabels[role].charAt(0);

  async function loadNotifications() {
    try { const response = await api.get("/notifications"); setNotificationList({ items: Array.isArray(response.data?.items) ? response.data.items : [], unreadCount: response.data?.unreadCount || 0 }); }
    catch { setNotificationList({ items: [], unreadCount: 0 }); }
  }
  useEffect(() => { if (user) loadNotifications(); }, [user?.id]);
  async function openNotification(notification) {
    if (!notification.readAt) {
      try { await api.post(`/notifications/${notification.id}/read`); await loadNotifications(); }
      catch { /* Keep navigation available if the read receipt cannot be saved. */ }
    }
    setNotificationsOpen(false);
    navigate(notification.targetPath);
  }

  return (
    <header className="sticky top-0 z-30 flex h-[72px] items-center justify-between gap-4 border-b border-slate-200 bg-white/95 px-4 backdrop-blur sm:px-6 lg:px-8">
      <div className="flex min-w-0 items-center gap-3">
        <Button variant="ghost" size="icon" className="lg:hidden" aria-label="Open navigation" onClick={onMenuClick}><Menu className="size-5" /></Button>
        <div className="hidden md:block">
          <SearchInput value={search} onChange={onSearchChange} placeholder="Search current dashboard…" label="Search current dashboard records" className="w-[min(360px,35vw)]" />
        </div>
      </div>

      <div className="flex shrink-0 items-center gap-1.5 sm:gap-2">
        <Badge tone="neutral" className="hidden sm:inline-flex">{roleLabels[role]}</Badge>
        <DropdownMenu open={notificationsOpen} onOpenChange={open => { setNotificationsOpen(open); if (open) loadNotifications(); }}>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon" aria-label="Notifications" className="relative text-slate-500">
              <Bell className="size-[18px]" />
              {notificationList.unreadCount > 0 && <span className="absolute right-1.5 top-1.5 grid min-h-4 min-w-4 place-items-center rounded-full bg-blue-600 px-1 text-[9px] font-semibold text-white ring-2 ring-white">{notificationList.unreadCount > 9 ? "9+" : notificationList.unreadCount}</span>}
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-80">
            <DropdownMenuLabel className="flex items-center justify-between">Notifications <span className="text-[10px] font-normal text-slate-400">{notificationList.unreadCount} unread</span></DropdownMenuLabel>
            <DropdownMenuSeparator />
            {!notificationList.items.length && <DropdownMenuItem disabled className="justify-center text-xs text-slate-400">You’re all caught up.</DropdownMenuItem>}
            {notificationList.items.map(item => <DropdownMenuItem key={item.id} onSelect={() => openNotification(item)} className="items-start"><span className={`mt-1.5 size-2 shrink-0 rounded-full ${item.readAt ? "bg-slate-300" : "bg-blue-500"}`} /><span className="min-w-0"><span className="block font-medium text-slate-800">{item.title}</span><span className="mt-0.5 block whitespace-normal text-xs text-slate-500">{item.message}</span><span className="mt-1 block text-[10px] text-slate-400">{new Date(item.createdAt).toLocaleString()}</span></span></DropdownMenuItem>)}
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
