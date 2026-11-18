import { SearchX } from "lucide-react";

export function EmptyState({ title = "No records found", description = "Try changing your search or filters." }) {
  return (
    <div className="flex min-h-44 flex-col items-center justify-center px-6 py-10 text-center">
      <span className="mb-3 grid size-10 place-items-center rounded-full bg-slate-100 text-slate-500"><SearchX className="size-5" /></span>
      <h3 className="text-sm font-semibold text-slate-800">{title}</h3>
      <p className="mt-1 max-w-xs text-xs leading-5 text-slate-500">{description}</p>
    </div>
  );
}
