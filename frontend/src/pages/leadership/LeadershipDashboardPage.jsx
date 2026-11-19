import { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { Bar, BarChart, CartesianGrid, Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { ChartContainer } from "@/components/crm/ChartContainer";
import { EmptyState } from "@/components/crm/EmptyState";
import { PageHeader } from "@/components/crm/PageHeader";
import { StatCard } from "@/components/crm/StatCard";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";

const blank = { totalOrganisations: 0, totalContacts: 0, activeLeads: 0, totalSqls: 0, totalOpportunities: 0, totalWonCustomers: 0, totalLostOpportunities: 0, totalRevenue: 0 };
const currentYear = new Date().getFullYear();
const fmtMoney = value => new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 0 }).format(Number(value || 0));
const problem = e => e.response?.data?.message || "Could not load analytics. Please retry.";
const views = {
  overview: ["Executive overview", "Organisation-wide results and headline sales metrics."],
  performance: ["People performance", "Compare individual BDR and AE outcomes for the selected period."],
  revenue: ["Revenue analytics", "Review won revenue by time period, account executive, and source."],
  departments: ["Department analytics", "Compare discovery, review, and opportunity outcomes across departments."],
  funnel: ["Sales funnel", "See how records progress from organisation discovery through deal closure."],
};

export function LeadershipDashboardPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const view = location.pathname.split("/")[2] || "overview";
  const [options, setOptions] = useState({ employees: [], departments: [], opportunityStages: [] });
  const [overview, setOverview] = useState(blank);
  const [bdrs, setBdrs] = useState([]);
  const [managers, setManagers] = useState([]);
  const [aes, setAes] = useState([]);
  const [funnel, setFunnel] = useState([]);
  const [revenue, setRevenue] = useState({ monthly: [], quarterly: [], byAe: [], bySource: [] });
  const [filters, setFilters] = useState({ fromDate: "", toDate: "", year: String(currentYear), month: "", quarter: "", department: "", employeeId: "", stage: "" });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [optionsError, setOptionsError] = useState("");

  useEffect(() => { api.get("/leadership/analytics/filters").then(r => setOptions(r.data)).catch(e => setOptionsError(problem(e))); }, []);
  useEffect(() => {
    let active = true;
    const params = Object.fromEntries(Object.entries(filters).filter(([key, value]) => value && !(key === "year" && !filters.month && !filters.quarter)));
    setLoading(true); setError("");
    Promise.all([
      api.get("/leadership/analytics/overview", { params }), api.get("/leadership/analytics/bdrs", { params }),
      api.get("/leadership/analytics/sales-managers", { params }), api.get("/leadership/analytics/aes", { params }),
      api.get("/leadership/analytics/funnel", { params }), api.get("/leadership/analytics/revenue", { params }),
    ]).then(([o, b, m, a, f, r]) => {
      if (!active) return;
      setOverview(o.data); setBdrs(b.data); setManagers(m.data); setAes(a.data); setFunnel(f.data); setRevenue(r.data);
    }).catch(e => { if (active) setError(problem(e)); }).finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [filters]);

  const cards = useMemo(() => [
    ["Organisations", overview.totalOrganisations, "building", "blue"], ["Contacts", overview.totalContacts, "users", "violet"],
    ["Active leads", overview.activeLeads, "target", "blue"], ["SQLs", overview.totalSqls, "review", "amber"],
    ["Opportunities", overview.totalOpportunities, "pipeline", "blue"], ["Won customers", overview.totalWonCustomers, "handover", "green"],
    ["Lost opportunities", overview.totalLostOpportunities, "activity", "amber"], ["Revenue", fmtMoney(overview.totalRevenue), "revenue", "green"],
  ], [overview]);
  const departmentCards = useMemo(() => [
    { name: "Business Development", staff: bdrs.length, metric: `${sum(bdrs, "organisationsDiscovered")} organisations`, detail: `${sum(bdrs, "contactsCollected")} contacts · ${sum(bdrs, "callsCompleted")} calls · ${sum(bdrs, "sqlsGenerated")} SQLs` },
    { name: "Sales Management", staff: managers.length, metric: `${sum(managers, "opportunitiesAllocated")} opportunities allocated`, detail: `${sum(managers, "sqlsReviewed")} SQLs reviewed · ${sum(managers, "sqlsApproved")} approved` },
    { name: "Account Executives", staff: aes.length, metric: `${sum(aes, "totalAssigned")} assigned opportunities`, detail: `${sum(aes, "won")} won · ${sum(aes, "lost")} lost · ${fmtMoney(sum(aes, "revenue"))} revenue` },
  ], [bdrs, managers, aes]);
  const page = views[view] ? view : "overview";
  function change(key, value) { setFilters(current => ({ ...current, [key]: value, ...(key === "month" && value ? { quarter: "" } : {}), ...(key === "quarter" && value ? { month: "" } : {}) })); }
  function clearFilters() { setFilters({ fromDate: "", toDate: "", year: String(currentYear), month: "", quarter: "", department: "", employeeId: "", stage: "" }); }

  return <>
    <PageHeader title={views[page][0]} description={views[page][1]} breadcrumb={["Workspace", "Leadership", views[page][0]]}/>
    <section aria-label="Analytics filters" className="mb-5 grid gap-3 rounded-xl border bg-white p-4 sm:grid-cols-2 lg:grid-cols-4 xl:grid-cols-8">
      <Filter label="From" type="date" value={filters.fromDate} onChange={v=>change("fromDate",v)}/>
      <Filter label="To" type="date" value={filters.toDate} onChange={v=>change("toDate",v)}/>
      <Filter label="Month" value={filters.month} onChange={v=>change("month",v)}><option value="">All months</option>{Array.from({length:12},(_,i)=><option key={i} value={i+1}>{new Date(2000,i,1).toLocaleString("en",{month:"long"})}</option>)}</Filter>
      <Filter label="Quarter" value={filters.quarter} onChange={v=>change("quarter",v)}><option value="">All quarters</option>{[1,2,3,4].map(q=><option key={q} value={q}>Q{q}</option>)}</Filter>
      <Filter label="Year" type="number" min="2000" max="2100" value={filters.year} onChange={v=>change("year",v)}/>
      <Filter label="Department" value={filters.department} onChange={v=>change("department",v)}><option value="">All departments</option>{options.departments.map(d=><option key={d} value={d}>{d.replaceAll("_"," ")}</option>)}</Filter>
      <Filter label="Employee" value={filters.employeeId} onChange={v=>change("employeeId",v)}><option value="">All employees</option>{options.employees.map(e=><option key={e.id} value={e.id}>{e.fullName} · {e.role}</option>)}</Filter>
      <Filter label="Opportunity stage" value={filters.stage} onChange={v=>change("stage",v)}><option value="">All stages</option>{options.opportunityStages.map(s=><option key={s} value={s}>{s}</option>)}</Filter>
      <div className="flex items-end"><Button variant="outline" className="w-full" onClick={clearFilters}>Clear filters</Button></div>
    </section>
    {optionsError && <p role="alert" className="mb-4 rounded-md bg-amber-50 p-3 text-sm text-amber-800">Some filter options could not be loaded: {optionsError}</p>}
    {error && <p role="alert" className="mb-4 rounded-md bg-rose-50 p-3 text-sm text-rose-700">{error}</p>}
    {loading && <p className="mb-3 text-sm text-slate-500">Updating analytics…</p>}
    {page === "overview" && <Overview cards={cards} onOpen={path => navigate(`/leadership/${path}`)}/>}
    {page === "performance" && <Performance bdrs={bdrs} aes={aes}/>}
    {page === "departments" && <Departments cards={departmentCards} managers={managers}/>}
    {page === "funnel" && <Funnel funnel={funnel}/>}
    {page === "revenue" && <Revenue revenue={revenue}/>}
    <p className="mt-5 text-xs text-slate-500">Date filters use Asia/Kolkata. Revenue uses final Won deal values and displays INR. Revival performance is unavailable until the revival workflow is implemented.</p>
  </>;
}

function Overview({cards,onOpen}) { return <>
  <section aria-label="Executive overview" className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">{cards.map(([label,value,icon,tone])=><StatCard key={label} label={label} value={value} icon={icon} tone={tone} change="Filtered CRM records"/>)}</section>
  <section className="mt-5 rounded-xl border bg-white p-5"><h2 className="text-sm font-semibold text-slate-900">Explore leadership insights</h2><p className="mt-1 text-sm text-slate-500">Open a focused view for people performance, department results, sales funnel, or revenue trends.</p><div className="mt-4 flex flex-wrap gap-2">{[["Performance","performance"],["Departments","departments"],["Sales funnel","funnel"],["Revenue","revenue"]].map(([label,path])=><Button key={path} variant="outline" onClick={()=>onOpen(path)}>{label}</Button>)}</div></section>
</>; }

function Performance({bdrs,aes}) { return <div className="space-y-6">
  <section><TableHeading title="BDR performance" note="SQL conversion = approved SQLs ÷ submitted SQLs."/><TableWrap headers={["BDR","Organisations","Contacts","Calls","Interested prospects","SQLs","Conversion"]} rows={bdrs.map(r=>[r.fullName,r.organisationsDiscovered,r.contactsCollected,r.callsCompleted,r.interestedProspects,r.sqlsGenerated,`${Number(r.sqlConversionPercent||0).toFixed(1)}%`])}/></section>
  <section><TableHeading title="AE performance" note="Conversion = won ÷ (won + lost). Revenue uses final Won deal values."/><TableWrap headers={["AE","Assigned","L1","L2","L3","Won","Lost","Conversion","Revenue"]} rows={aes.map(r=>[r.fullName,r.totalAssigned,r.l1,r.l2,r.l3,r.won,r.lost,`${Number(r.conversionPercent||0).toFixed(1)}%`,fmtMoney(r.revenue)])}/></section>
</div>; }

function Departments({cards,managers}) { return <div className="space-y-6">
  <section aria-label="Department summaries" className="grid gap-4 lg:grid-cols-3">{cards.map(c=><article key={c.name} className="rounded-xl border bg-white p-5"><p className="text-xs font-medium uppercase tracking-wide text-slate-500">{c.name}</p><p className="mt-3 text-2xl font-semibold text-slate-900">{c.staff} <span className="text-sm font-normal text-slate-500">employees</span></p><p className="mt-4 text-sm font-medium text-slate-800">{c.metric}</p><p className="mt-1 text-xs leading-5 text-slate-500">{c.detail}</p></article>)}</section>
  <section><TableHeading title="Sales Manager performance" note="Assignment time is average hours from SQL approval to first AE assignment. Revival data will appear when that workflow is implemented."/><TableWrap headers={["Sales Manager","SQLs reviewed","SQLs approved","Approval rate","Avg assignment time","Opportunities allocated","Revival"]} rows={managers.map(r=>[r.fullName,r.sqlsReviewed,r.sqlsApproved,`${Number(r.sqlApprovalRate||0).toFixed(1)}%`,`${Number(r.averageAssignmentHours||0).toFixed(1)} h`,r.opportunitiesAllocated,r.expiredAccountRevivalPerformance??"Not configured"])}/></section>
</div>; }

function Funnel({funnel}) { const stages=funnel.filter(item=>["L1","L2","L3","WON","LOST"].includes(item.stage)); return <div className="grid gap-4 xl:grid-cols-2">
  <ChartCard title="End-to-end sales funnel" description="Counts at each step for the selected filters.">{!funnel.length?<EmptyState title="No funnel data" description="Adjust the date or employee filters."/>:<ResponsiveContainer width="100%" height={440}><BarChart data={funnel} layout="vertical" margin={{left:16,right:16}}><CartesianGrid strokeDasharray="3 3" horizontal={false}/><XAxis type="number" allowDecimals={false}/><YAxis dataKey="stage" type="category" width={145} tick={{fontSize:11}}/><Tooltip/><Bar dataKey="count" name="Records" fill="#0b3b24" radius={[0,5,5,0]}/></BarChart></ResponsiveContainer>}</ChartCard>
  <ChartCard title="Opportunity stage distribution" description="Current pipeline distribution across L1, L2, L3, Won, and Lost.">{!stages.length?<EmptyState title="No opportunity stages" description="Stage counts appear when opportunities exist."/>:<ResponsiveContainer width="100%" height={380}><BarChart data={stages} margin={{left:8,right:12,bottom:20}}><CartesianGrid strokeDasharray="3 3"/><XAxis dataKey="stage"/><YAxis allowDecimals={false}/><Tooltip/><Bar dataKey="count" name="Opportunities" fill="#7c3aed" radius={[5,5,0,0]}/></BarChart></ResponsiveContainer>}</ChartCard>
</div>; }

function Revenue({revenue}) { return <div className="space-y-4">
  <div className="grid gap-4 xl:grid-cols-2">
    <ChartCard title="Monthly revenue" description="Won revenue grouped by close month.">{!revenue.monthly?.length?<EmptyState title="No won revenue in this period" description="Revenue appears after an AE closes an opportunity as won."/>:<ResponsiveContainer width="100%" height={300}><LineChart data={revenue.monthly} margin={{left:8,right:16}}><CartesianGrid strokeDasharray="3 3"/><XAxis dataKey="period" tick={{fontSize:11}}/><YAxis tickFormatter={v=>`${Math.round(v/1000)}k`} tick={{fontSize:11}}/><Tooltip formatter={v=>fmtMoney(v)}/><Line type="monotone" dataKey="revenue" name="Revenue" stroke="#0b3b24" strokeWidth={2.5} dot={false}/></LineChart></ResponsiveContainer>}</ChartCard>
    <ChartCard title="Quarterly revenue" description="Won revenue grouped by close quarter.">{!revenue.quarterly?.length?<EmptyState title="No quarterly revenue" description="Won deal revenue appears here."/>:<ResponsiveContainer width="100%" height={300}><BarChart data={revenue.quarterly} margin={{left:8,right:16}}><CartesianGrid strokeDasharray="3 3"/><XAxis dataKey="period" tick={{fontSize:11}}/><YAxis tickFormatter={v=>`${Math.round(v/1000)}k`} tick={{fontSize:11}}/><Tooltip formatter={v=>fmtMoney(v)}/><Bar dataKey="revenue" name="Revenue" fill="#0b3b24" radius={[5,5,0,0]}/></BarChart></ResponsiveContainer>}</ChartCard>
  </div>
  <div className="grid gap-4 xl:grid-cols-2">
    <ChartCard title="Revenue by AE" description="Final revenue from won opportunities.">{!revenue.byAe?.length?<EmptyState title="No AE revenue yet" description="Won deal revenue will appear here."/>:<ResponsiveContainer width="100%" height={280}><BarChart data={revenue.byAe} margin={{left:8,right:12,bottom:35}}><CartesianGrid strokeDasharray="3 3"/><XAxis dataKey="fullName" angle={-20} textAnchor="end" interval={0} tick={{fontSize:10}}/><YAxis tickFormatter={v=>`${Math.round(v/1000)}k`} tick={{fontSize:11}}/><Tooltip formatter={v=>fmtMoney(v)}/><Bar dataKey="revenue" name="Revenue" fill="#16a34a" radius={[5,5,0,0]}/></BarChart></ResponsiveContainer>}</ChartCard>
    <ChartCard title="Revenue by source" description="Lead source comes from the organisation record.">{!revenue.bySource?.length?<EmptyState title="No source revenue yet" description="Won revenue is grouped by organisation lead source."/>:<ResponsiveContainer width="100%" height={280}><BarChart data={revenue.bySource} layout="vertical" margin={{left:12,right:14}}><CartesianGrid strokeDasharray="3 3" horizontal={false}/><XAxis type="number" tickFormatter={v=>`${Math.round(v/1000)}k`} tick={{fontSize:11}}/><YAxis type="category" dataKey="source" width={110} tick={{fontSize:11}}/><Tooltip formatter={v=>fmtMoney(v)}/><Bar dataKey="revenue" name="Revenue" fill="#7c3aed" radius={[0,5,5,0]}/></BarChart></ResponsiveContainer>}</ChartCard>
  </div>
</div>; }

function sum(rows,key) { return rows.reduce((total,row)=>total+Number(row[key]||0),0); }
function Filter({label,type="select",value,onChange,children,...props}) { return <label className="block text-xs font-medium text-slate-500"><span>{label}</span>{type==="select"?<select value={value} onChange={e=>onChange(e.target.value)} className="mt-1.5 h-9 w-full rounded-md border border-slate-200 bg-white px-2 text-sm text-slate-800">{children}</select>:<input type={type} value={value} onChange={e=>onChange(e.target.value)} {...props} className="mt-1.5 h-9 w-full rounded-md border border-slate-200 px-2 text-sm text-slate-800"/>}</label>; }
function ChartCard({title,description,children}) { return <section className="min-w-0 rounded-xl border bg-white p-4"><h2 className="text-sm font-semibold text-slate-900">{title}</h2><p className="mt-1 text-xs text-slate-500">{description}</p><div className="mt-4 min-h-[280px]">{children}</div></section>; }
function TableHeading({title,note}) { return <div className="mb-3"><h2 className="text-sm font-semibold text-slate-900">{title}</h2><p className="mt-1 text-xs text-slate-500">{note}</p></div>; }
function TableWrap({headers,rows}) { return <div className="overflow-x-auto rounded-xl border bg-white">{!rows.length?<EmptyState title="No matching records" description="No employee analytics match the selected filters."/>:<table className="w-full min-w-[760px] text-left text-sm"><thead className="bg-slate-50 text-xs text-slate-500"><tr>{headers.map(h=><th key={h} className="px-4 py-3 font-medium">{h}</th>)}</tr></thead><tbody>{rows.map((row,i)=><tr key={`${row[0]}-${i}`} className="border-t">{row.map((cell,j)=><td key={j} className="px-4 py-3 text-slate-700">{cell??"—"}</td>)}</tr>)}</tbody></table>}</div>; }
