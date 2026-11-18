import { useMemo, useState } from "react";
import { useOutletContext } from "react-router-dom";
import { CalendarDays } from "lucide-react";
import { ChartContainer } from "@/components/crm/ChartContainer";
import { DataTable } from "@/components/crm/DataTable";
import { FilterDropdown } from "@/components/crm/FilterDropdown";
import { PageHeader } from "@/components/crm/PageHeader";
import { SearchInput } from "@/components/crm/SearchInput";
import { StatCard } from "@/components/crm/StatCard";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { roleLabels } from "@/config/roleNavigation";
import { chartSeries, dashboardData } from "@/data/dashboardData";

const pageSize = 3;

export function DashboardPage({ role }) {
  const data = dashboardData[role];
  const { search, setSearch } = useOutletContext();
  const [status, setStatus] = useState("all");
  const [page, setPage] = useState(1);

  const statusOptions = useMemo(() => [
    { value: "all", label: "All statuses" },
    ...Array.from(new Set(data.rows.map((row) => row.status))).map((value) => ({ value, label: value })),
  ], [data.rows]);

  const filteredRows = useMemo(() => {
    const normalized = search.trim().toLowerCase();
    return data.rows.filter((row) => {
      const matchesSearch = !normalized || Object.values(row).some((value) => String(value).toLowerCase().includes(normalized));
      const matchesStatus = status === "all" || row.status === status;
      return matchesSearch && matchesStatus;
    });
  }, [data.rows, search, status]);

  const pageCount = Math.max(1, Math.ceil(filteredRows.length / pageSize));
  const currentPage = Math.min(page, pageCount);
  const visibleRows = filteredRows.slice((currentPage - 1) * pageSize, currentPage * pageSize);

  function updateSearch(value) {
    setSearch(value);
    setPage(1);
  }

  function updateStatus(value) {
    setStatus(value);
    setPage(1);
  }

  return (
    <>
      <PageHeader
        breadcrumb={["Workspace", roleLabels[role], "Dashboard"]}
        title={data.title}
        description={data.description}
        action={<Badge tone="neutral" className="gap-1.5 px-3 py-2 text-xs"><CalendarDays className="size-3.5" /> Last 30 days</Badge>}
      />

      <Card className="mb-5 border-blue-100 bg-blue-50/70 shadow-none">
        <CardContent className="flex flex-col gap-1.5 px-4 py-3 sm:flex-row sm:items-center sm:justify-between">
          <p className="text-xs font-medium text-blue-900">Preview data — numbers and names are illustrative placeholders, not CRM records.</p>
          <p className="text-[11px] text-blue-700">No business actions are connected yet.</p>
        </CardContent>
      </Card>

      <section aria-label={`${roleLabels[role]} dashboard metrics`} className="grid grid-cols-1 gap-3 sm:grid-cols-2 xl:grid-cols-4 xl:gap-4">
        {data.metrics.map((metric) => <StatCard key={metric.label} {...metric} />)}
      </section>

      <section aria-label="Dashboard analytics" className="mt-4 grid grid-cols-1 gap-4 xl:grid-cols-[minmax(0,1.65fr)_minmax(260px,1fr)]">
        <ChartContainer title={data.chartTitle} description={data.chartDescription} data={data.chart} series={chartSeries[role]} />
        <Card className="min-w-0">
          <div className="border-b border-slate-100 p-5">
            <p className="text-sm font-semibold text-slate-900">Quarterly focus</p>
            <p className="mt-1 text-xs text-slate-500">A sample progress snapshot for this role</p>
          </div>
          <CardContent className="space-y-5 p-5">
            <ProgressRow label={role === "BDR" ? "Discovery goal" : role === "AE" ? "Opportunity goal" : "Team target"} value={role === "LEADERSHIP" ? 78 : 72} color="bg-blue-600" />
            <ProgressRow label="Follow-up completion" value={64} color="bg-violet-500" />
            <ProgressRow label="On-time activity" value={86} color="bg-emerald-500" />
            <div className="rounded-lg bg-slate-50 p-3.5">
              <p className="text-xs font-medium text-slate-700">Next checkpoint</p>
              <p className="mt-1 text-xs leading-5 text-slate-500">Review team progress and upcoming commitments for the current period.</p>
            </div>
          </CardContent>
        </Card>
      </section>

      <section className="mt-4">
        <div className="mb-3 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div><h2 className="text-sm font-semibold text-slate-900">{data.tableTitle}</h2><p className="mt-1 text-xs text-slate-500">A preview of the records this role works with.</p></div>
          <div className="flex flex-col gap-2 sm:flex-row">
            <SearchInput value={search} onChange={updateSearch} placeholder="Search this list…" label="Search dashboard records" className="md:hidden" />
            <FilterDropdown value={status} onValueChange={updateStatus} options={statusOptions} />
          </div>
        </div>
        <DataTable
          title={data.tableTitle}
          rows={visibleRows}
          page={currentPage}
          pageCount={pageCount}
          onPageChange={setPage}
          totalCount={filteredRows.length}
        />
      </section>
    </>
  );
}

function ProgressRow({ label, value, color }) {
  return (
    <div>
      <div className="mb-2 flex items-center justify-between text-xs"><span className="font-medium text-slate-700">{label}</span><span className="text-slate-500">{value}%</span></div>
      <div className="h-1.5 overflow-hidden rounded-full bg-slate-100"><div className={`h-full rounded-full ${color}`} style={{ width: `${value}%` }} /></div>
    </div>
  );
}
