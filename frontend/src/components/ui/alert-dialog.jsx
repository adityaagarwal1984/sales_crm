import * as AlertDialogPrimitive from "@radix-ui/react-alert-dialog";
import { cn } from "@/lib/utils";

export const AlertDialog = AlertDialogPrimitive.Root;
export const AlertDialogTrigger = AlertDialogPrimitive.Trigger;

export function AlertDialogContent({ className, ...props }) {
  return (
    <AlertDialogPrimitive.Portal>
      <AlertDialogPrimitive.Overlay className="fixed inset-0 z-50 bg-slate-950/30 backdrop-blur-[1px]" />
      <AlertDialogPrimitive.Content className={cn("fixed left-1/2 top-1/2 z-50 w-[calc(100%-2rem)] max-w-md -translate-x-1/2 -translate-y-1/2 rounded-xl border border-slate-200 bg-white p-6 shadow-xl outline-none", className)} {...props} />
    </AlertDialogPrimitive.Portal>
  );
}

export const AlertDialogTitle = ({ className, ...props }) => (
  <AlertDialogPrimitive.Title className={cn("text-base font-semibold text-slate-950", className)} {...props} />
);

export const AlertDialogDescription = ({ className, ...props }) => (
  <AlertDialogPrimitive.Description className={cn("mt-2 text-sm leading-6 text-slate-500", className)} {...props} />
);

export const AlertDialogCancel = ({ className, ...props }) => (
  <AlertDialogPrimitive.Cancel className={cn("inline-flex h-9 items-center justify-center rounded-md border border-slate-200 px-3.5 text-sm font-medium text-slate-700 hover:bg-slate-50", className)} {...props} />
);

export const AlertDialogAction = ({ className, ...props }) => (
  <AlertDialogPrimitive.Action className={cn("inline-flex h-9 items-center justify-center rounded-md bg-rose-600 px-3.5 text-sm font-medium text-white hover:bg-rose-700", className)} {...props} />
);
