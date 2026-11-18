import {
  Activity, Building2, CalendarDays, CircleDollarSign, ClipboardCheck,
  Handshake, Phone, Target, TrendingUp, Users,
} from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { cn } from "@/lib/utils";

const icons = {
  activity: Activity, building: Building2, calendar: CalendarDays,
  phone: Phone, pipeline: TrendingUp, review: ClipboardCheck,
  revenue: CircleDollarSign, handover: Handshake, target: Target, users: Users,
};

const tones = {
  blue: "bg-blue-50 text-blue-700 ring-blue-100",
  green: "bg-emerald-50 text-emerald-700 ring-emerald-100",
  amber: "bg-amber-50 text-amber-700 ring-amber-100",
  violet: "bg-violet-50 text-violet-700 ring-violet-100",
};

export function StatCard({ label, value, change, icon, tone = "blue" }) {
  const Icon = icons[icon] ?? Activity;

  return (
    <Card className="min-w-0">
      <CardContent className="p-4 sm:p-5">
        <div className="flex items-start justify-between gap-3">
          <p className="text-sm font-medium text-slate-500">{label}</p>
          <span className={cn("grid size-9 shrink-0 place-items-center rounded-lg ring-1 ring-inset", tones[tone] ?? tones.blue)}>
            <Icon className="size-4" aria-hidden="true" />
          </span>
        </div>
        <p className="mt-3 text-2xl font-semibold tracking-tight text-slate-950 sm:text-[28px]">{value}</p>
        <p className="mt-1.5 text-xs text-slate-500">{change}</p>
      </CardContent>
    </Card>
  );
}
