import { ArrowUpDown } from "lucide-react";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { EmptyState } from "@/components/crm/EmptyState";
import { Pagination } from "@/components/crm/Pagination";
import { StatusBadge } from "@/components/crm/StatusBadge";

const columns = [
  { key: "name", label: "Name" },
  { key: "action", label: "Activity" },
  { key: "status", label: "Status" },
  { key: "date", label: "Updated" },
];

export function DataTable({ title, rows, page, pageCount, onPageChange, totalCount }) {
  return (
    <Card className="min-w-0 overflow-hidden">
      <CardHeader className="flex-row items-center justify-between border-b border-slate-100 pb-4">
        <div><CardTitle>{title}</CardTitle><CardDescription className="mt-1">{totalCount} illustrative records</CardDescription></div>
        <Button variant="ghost" size="sm" className="hidden text-slate-500 sm:inline-flex">View all <ArrowUpDown className="size-3.5" /></Button>
      </CardHeader>
      <CardContent className="p-0">
        {!rows.length ? <EmptyState /> : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[680px] text-left text-sm">
              <thead><tr className="bg-slate-50/70 text-[11px] font-medium uppercase tracking-wide text-slate-400">
                {columns.map((column) => <th key={column.key} scope="col" className="px-5 py-3.5">{column.label}</th>)}
              </tr></thead>
              <tbody className="divide-y divide-slate-100">
                {rows.map((row) => (
                  <tr key={`${row.name}-${row.date}`} className="transition-colors hover:bg-slate-50/70">
                    <td className="px-5 py-3.5"><p className="font-medium text-slate-800">{row.name}</p><p className="mt-0.5 text-xs text-slate-400">{row.detail}</p></td>
                    <td className="px-5 py-3.5 text-xs text-slate-600">{row.action}</td>
                    <td className="px-5 py-3.5"><StatusBadge status={row.status} /></td>
                    <td className="px-5 py-3.5 text-xs text-slate-500">{row.date}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        <div className="px-5 py-3.5">
          <Pagination page={page} pageCount={pageCount} onPageChange={onPageChange} itemCount={rows.length} />
        </div>
      </CardContent>
    </Card>
  );
}
