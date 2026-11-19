import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ArrowRight, BarChart3, ClipboardCheck, UserRoundCheck } from "lucide-react";
import { PageHeader } from "@/components/crm/PageHeader";
import { StatCard } from "@/components/crm/StatCard";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";

const blank = { totalBdrs: 0, totalAes: 0, sqlsPendingReview: 0, sqlsApproved: 0, opportunitiesAssigned: 0, activeOpportunities: 0, expiredCustomerAccounts: null, teamConversionRate: 0 };
export function SalesManagerDashboardPage() {
  const [data, setData] = useState(blank);
  const [error, setError] = useState("");
  useEffect(() => { api.get("/sales-manager/dashboard").then(r => setData(r.data)).catch(() => setError("Could not load the team dashboard.")); }, []);
  const metrics = [
    ["Total BDRs", data.totalBdrs, "users", "blue", "All CRM BDR accounts"],
    ["Total AEs", data.totalAes, "users", "violet", "All CRM AE accounts"],
    ["SQLs Pending Review", data.sqlsPendingReview, "review", "amber", "Submitted by all BDRs"],
    ["SQLs Approved", data.sqlsApproved, "review", "green", "Ready for AE assignment"],
    ["Opportunities Assigned", data.opportunitiesAssigned, "pipeline", "blue", "Current and closed"],
    ["Active Opportunities", data.activeOpportunities, "target", "violet", "Open AE work"],
    ["Expired Customer Accounts", data.expiredCustomerAccounts ?? "Not configured", "building", "amber", data.expiredCustomerAccounts == null ? "Customer lifecycle is not defined" : "Past account expiry date"],
    ["Team Conversion Rate", `${Number(data.teamConversionRate || 0).toFixed(1)}%`, "activity", "green", "Won opportunities ÷ assigned opportunities"],
  ];
  return <>
    <PageHeader title="Sales Manager dashboard" description="Review SQLs from all BDRs, assignments, and sales performance." breadcrumb={["Workspace", "Sales Manager", "Dashboard"]}/>
    {error && <p role="alert" className="mb-4 rounded-md bg-rose-50 p-3 text-sm text-rose-700">{error}</p>}
    <section aria-label="Team overview metrics" className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
      {metrics.map(([label, value, icon, tone, change]) => <StatCard key={label} label={label} value={value} icon={icon} tone={tone} change={change}/>) }
    </section>
    <section className="mt-6 grid gap-4 md:grid-cols-3">
      <QuickLink to="/sales-manager/sql-qualifications" icon={ClipboardCheck} title="Review SQLs" description={`${data.sqlsPendingReview} qualifications need your review.`}/>
      <QuickLink to="/sales-manager/assignments" icon={UserRoundCheck} title="Assign opportunities" description={`${data.sqlsApproved} approved SQLs are ready for assignment.`}/>
      <QuickLink to="/sales-manager/team-performance" icon={BarChart3} title="Team performance" description="Compare BDR discovery and AE outcomes."/>
    </section>
    <p className="mt-5 text-xs text-slate-500">All Sales Managers can access CRM BDR and AE accounts. Conversion is provisionally calculated as won opportunities divided by all assigned opportunities.</p>
  </>;
}

function QuickLink({ to, icon: Icon, title, description }) {
  return <Link to={to} className="group rounded-xl border border-slate-200 bg-white p-5 shadow-sm transition hover:border-blue-200 hover:shadow">
    <div className="flex items-start justify-between"><span className="grid size-10 place-items-center rounded-lg bg-blue-50 text-blue-700"><Icon className="size-5"/></span><ArrowRight className="size-4 text-slate-400 transition group-hover:translate-x-0.5 group-hover:text-blue-600"/></div>
    <h2 className="mt-4 font-semibold text-slate-900">{title}</h2><p className="mt-1 text-sm text-slate-500">{description}</p>
    <Button variant="outline" size="sm" className="mt-4">Open</Button>
  </Link>;
}
