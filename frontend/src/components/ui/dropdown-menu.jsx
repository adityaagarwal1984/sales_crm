import * as DropdownMenuPrimitive from "@radix-ui/react-dropdown-menu";
import { Check, ChevronRight } from "lucide-react";
import { cn } from "@/lib/utils";

export const DropdownMenu = DropdownMenuPrimitive.Root;
export const DropdownMenuTrigger = DropdownMenuPrimitive.Trigger;
export const DropdownMenuGroup = DropdownMenuPrimitive.Group;
export const DropdownMenuPortal = DropdownMenuPrimitive.Portal;

export const DropdownMenuContent = ({ className, sideOffset = 8, ...props }) => (
  <DropdownMenuPrimitive.Portal>
    <DropdownMenuPrimitive.Content
      sideOffset={sideOffset}
      className={cn("z-50 min-w-48 overflow-hidden rounded-lg border border-slate-200 bg-white p-1.5 text-slate-700 shadow-popover outline-none", className)}
      {...props}
    />
  </DropdownMenuPrimitive.Portal>
);

export const DropdownMenuLabel = ({ className, ...props }) => (
  <DropdownMenuPrimitive.Label className={cn("px-2 py-1.5 text-xs font-semibold text-slate-500", className)} {...props} />
);

export const DropdownMenuSeparator = ({ className, ...props }) => (
  <DropdownMenuPrimitive.Separator className={cn("-mx-1 my-1 h-px bg-slate-100", className)} {...props} />
);

export const DropdownMenuItem = ({ className, ...props }) => (
  <DropdownMenuPrimitive.Item className={cn("relative flex cursor-pointer select-none items-center gap-2 rounded-md px-2 py-2 text-sm outline-none transition-colors focus:bg-slate-100 focus:text-slate-950 data-[disabled]:pointer-events-none data-[disabled]:opacity-50", className)} {...props} />
);

export const DropdownMenuCheckboxItem = ({ children, checked, className, ...props }) => (
  <DropdownMenuPrimitive.CheckboxItem checked={checked} className={cn("relative flex cursor-pointer select-none items-center rounded-md py-2 pl-8 pr-2 text-sm outline-none focus:bg-slate-100", className)} {...props}>
    <span className="absolute left-2 flex size-4 items-center justify-center"><DropdownMenuPrimitive.ItemIndicator><Check className="size-3.5" /></DropdownMenuPrimitive.ItemIndicator></span>
    {children}
  </DropdownMenuPrimitive.CheckboxItem>
);

export const DropdownMenuSubTrigger = ({ children, className, ...props }) => (
  <DropdownMenuPrimitive.SubTrigger className={cn("flex cursor-pointer select-none items-center rounded-md px-2 py-2 text-sm outline-none focus:bg-slate-100", className)} {...props}>
    {children}<ChevronRight className="ml-auto size-4" />
  </DropdownMenuPrimitive.SubTrigger>
);
