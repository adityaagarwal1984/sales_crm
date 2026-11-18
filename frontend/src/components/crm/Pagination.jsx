import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";

export function Pagination({ page, pageCount, onPageChange, itemCount }) {
  if (pageCount < 2) return <p className="text-xs text-slate-500">Showing {itemCount} records</p>;

  return (
    <div className="flex items-center justify-between gap-4 border-t border-slate-100 px-5 py-3.5">
      <p className="text-xs text-slate-500">Page {page} of {pageCount}</p>
      <div className="flex items-center gap-1">
        <Button aria-label="Previous page" variant="outline" size="icon" className="size-8" disabled={page <= 1} onClick={() => onPageChange(page - 1)}>
          <ChevronLeft className="size-4" />
        </Button>
        <Button aria-label="Next page" variant="outline" size="icon" className="size-8" disabled={page >= pageCount} onClick={() => onPageChange(page + 1)}>
          <ChevronRight className="size-4" />
        </Button>
      </div>
    </div>
  );
}
