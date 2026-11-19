import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { EmptyState } from "@/components/crm/EmptyState";
import { PageHeader } from "@/components/crm/PageHeader";
import { api } from "@/lib/api";

const problem = e => e.response?.data?.message || "Could not load team performance.";
export function TeamPerformancePage() {
  const [data, setData] = useState({ bdrs: [], aes: [] });
  const [error, setError] = useState("");
  useEffect(() => { api.get("/sales-manager/team-performance").then(r => setData({ bdrs: r.data.bdrs || [], aes: r.data.aes || [] })).catch(e => setError(problem(e))); }, []);
  return <>
    <PageHeader title="Team performance" description="Compare discovery activity and AE outcomes across all BDR and AE accounts." breadcrumb={["Workspace", <Link key="manager" to="/sales-manager">Sales Manager</Link>, "Team performance"]}/>
    {error && <p role="alert" className="mb-4 rounded-md bg-rose-50 p-3 text-sm text-rose-700">{error}</p>}
    <section className="mb-6"><div className="mb-3"><h2 className="font-semibold text-slate-900">BDR performance</h2><p className="mt-1 text-xs text-slate-500">All-time activity across every BDR account.</p></div><div className="overflow-x-auto rounded-xl border bg-white">{!data.bdrs.length ? <EmptyState title="No BDR accounts" description="BDRs created by an administrator appear here."/> : <table className="w-full min-w-[720px] text-left text-sm"><thead className="bg-slate-50 text-xs text-slate-500"><tr>{["BDR","Organisations discovered","Contacts added","Calls completed","SQLs generated"].map(t=><th className="px-4 py-3 font-medium" key={t}>{t}</th>)}</tr></thead><tbody>{data.bdrs.map(row=><tr key={row.userId} className="border-t"><td className="px-4 py-3 font-medium">{row.fullName}</td><td className="px-4 py-3">{row.organisationsDiscovered}</td><td className="px-4 py-3">{row.contactsAdded}</td><td className="px-4 py-3">{row.callsCompleted}</td><td className="px-4 py-3">{row.sqlsGenerated}</td></tr>)}</tbody></table>}</div></section>
    <section><div className="mb-3"><h2 className="font-semibold text-slate-900">AE performance</h2><p className="mt-1 text-xs text-slate-500">Assigned opportunity, completed meeting, win, and revenue totals across every AE account.</p></div><div className="overflow-x-auto rounded-xl border bg-white">{!data.aes.length ? <EmptyState title="No AE accounts" description="AEs created by an administrator appear here."/> : <table className="w-full min-w-[760px] text-left text-sm"><thead className="bg-slate-50 text-xs text-slate-500"><tr>{["AE","Opportunities assigned","Meetings completed","Deals won","Revenue generated"].map(t=><th className="px-4 py-3 font-medium" key={t}>{t}</th>)}</tr></thead><tbody>{data.aes.map(row=><tr key={row.userId} className="border-t"><td className="px-4 py-3 font-medium">{row.fullName}</td><td className="px-4 py-3">{row.opportunitiesAssigned}</td><td className="px-4 py-3">{row.meetingsCompleted}</td><td className="px-4 py-3">{row.dealsWon}</td><td className="px-4 py-3">{money(row.revenueGenerated,row.currencyCode)}</td></tr>)}</tbody></table>}</div></section>
    <p className="mt-4 text-xs text-slate-500">SQL generation counts submitted qualifications, including those later approved or rejected. Revenue is the total value of won opportunities; currency is provisionally INR.</p>
  </>;
}
function money(value,currency="INR") { return new Intl.NumberFormat("en-IN",{style:"currency",currency,maximumFractionDigits:0}).format(Number(value||0)); }
