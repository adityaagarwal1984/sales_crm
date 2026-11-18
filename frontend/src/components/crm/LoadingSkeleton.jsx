import { Skeleton } from "@/components/ui/skeleton";

export function LoadingSkeleton({ rows = 4 }) {
  return (
    <div role="status" aria-label="Loading dashboard" className="space-y-3 p-5">
      {Array.from({ length: rows }, (_, index) => (
        <div key={index} className="flex items-center gap-3">
          <Skeleton className="size-9 rounded-full" />
          <div className="flex-1 space-y-2"><Skeleton className="h-3 w-2/5" /><Skeleton className="h-2.5 w-3/5" /></div>
          <Skeleton className="h-6 w-20" />
        </div>
      ))}
    </div>
  );
}
