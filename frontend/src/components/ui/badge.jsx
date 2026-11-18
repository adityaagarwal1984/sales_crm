import { cn } from "@/lib/utils";

const badgeStyles = {
  neutral: "bg-slate-100 text-slate-700 ring-slate-200",
  blue: "bg-blue-50 text-blue-700 ring-blue-100",
  green: "bg-emerald-50 text-emerald-700 ring-emerald-100",
  amber: "bg-amber-50 text-amber-800 ring-amber-100",
  red: "bg-rose-50 text-rose-700 ring-rose-100",
  violet: "bg-violet-50 text-violet-700 ring-violet-100",
};

export function Badge({ className, tone = "neutral", ...props }) {
  return (
    <span
      className={cn("inline-flex items-center gap-1 rounded-full px-2.5 py-1 text-[11px] font-medium leading-none ring-1 ring-inset", badgeStyles[tone] ?? badgeStyles.neutral, className)}
      {...props}
    />
  );
}
