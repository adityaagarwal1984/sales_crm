import {
  Activity,
  BarChart3,
  Building2,
  CalendarDays,
  CircleDollarSign,
  ClipboardCheck,
  ContactRound,
  Gauge,
  Handshake,
  LayoutDashboard,
  Phone,
  Settings2,
  Target,
  Users,
  UserRoundCheck,
} from "lucide-react";

export const roleOrder = ["ADMIN", "BDR", "SALES_MANAGER", "AE", "LEADERSHIP"];

export const roleLabels = {
  ADMIN: "Administrator",
  BDR: "Business Development",
  SALES_MANAGER: "Sales Manager",
  AE: "Account Executive",
  LEADERSHIP: "Leadership",
};

const dashboardItem = { label: "Dashboard", icon: LayoutDashboard, available: true };

export const roleNavigation = {
  ADMIN: {
    dashboard: "admin",
    greeting: "Good morning, Aditya",
    items: [
      dashboardItem,
      { label: "Users & roles", icon: Users, href: "/admin/users", available: true },
      { label: "Targets", icon: Target },
      { label: "System settings", icon: Settings2 },
    ],
  },
  BDR: {
    dashboard: "bdr",
    greeting: "Good morning, Aditya",
    items: [
      dashboardItem,
      { label: "Organisations", icon: Building2 },
      { label: "Contacts", icon: ContactRound },
      { label: "Calls & follow-ups", icon: Phone },
      { label: "SQL submissions", icon: ClipboardCheck },
    ],
  },
  SALES_MANAGER: {
    dashboard: "sales-manager",
    greeting: "Good morning, Priya",
    items: [
      dashboardItem,
      { label: "SQL review", icon: ClipboardCheck },
      { label: "AE assignments", icon: UserRoundCheck },
      { label: "Team performance", icon: BarChart3 },
      { label: "Revival queue", icon: Activity },
    ],
  },
  AE: {
    dashboard: "ae",
    greeting: "Good morning, Rohan",
    items: [
      dashboardItem,
      { label: "Opportunities", icon: CircleDollarSign },
      { label: "Meetings", icon: CalendarDays },
      { label: "Stakeholders", icon: ContactRound },
      { label: "Handover", icon: Handshake },
    ],
  },
  LEADERSHIP: {
    dashboard: "leadership",
    greeting: "Good morning, Ananya",
    items: [
      dashboardItem,
      { label: "Performance", icon: Gauge },
      { label: "Revenue", icon: CircleDollarSign },
      { label: "Departments", icon: Users },
      { label: "Sales funnel", icon: BarChart3 },
    ],
  },
};
