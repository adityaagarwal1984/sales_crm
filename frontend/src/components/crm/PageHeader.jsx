import { ChevronRight } from "lucide-react";

export function PageHeader({ title, description, breadcrumb = ["Workspace"], action }) {
  return (
    <div className="mb-6 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
      <div>
        <nav aria-label="Breadcrumb" className="mb-2 flex items-center gap-1.5 text-xs text-slate-400">
          {breadcrumb.map((item, index) => (
            <span key={item} className="flex items-center gap-1.5">
              {index ? <ChevronRight className="size-3" aria-hidden="true" /> : null}
              <span className={index === breadcrumb.length - 1 ? "font-medium text-slate-600" : ""}>{item}</span>
            </span>
          ))}
        </nav>
        <h1 className="text-2xl font-semibold tracking-tight text-slate-950 sm:text-[28px]">{title}</h1>
        <p className="mt-1.5 text-sm text-slate-500">{description}</p>
      </div>
      {action ? <div className="shrink-0">{action}</div> : null}
    </div>
  );
}
