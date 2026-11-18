import { Input } from "@/components/ui/input";

export function FormField({ id, label, hint, error, as = "input", ...props }) {
  const Control = as === "textarea" ? "textarea" : Input;

  return (
    <div className="space-y-1.5">
      <label htmlFor={id} className="text-sm font-medium text-slate-700">{label}</label>
      <Control
        id={id}
        aria-invalid={Boolean(error)}
        aria-describedby={error ? `${id}-error` : hint ? `${id}-hint` : undefined}
        className={as === "textarea" ? "min-h-24 w-full rounded-md border border-slate-200 bg-white px-3 py-2 text-sm outline-none focus:border-blue-400 focus:ring-2 focus:ring-blue-100" : undefined}
        {...props}
      />
      {hint && !error ? <p id={`${id}-hint`} className="text-xs text-slate-500">{hint}</p> : null}
      {error ? <p id={`${id}-error`} className="text-xs text-rose-600">{error}</p> : null}
    </div>
  );
}
