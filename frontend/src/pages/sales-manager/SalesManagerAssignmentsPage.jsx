import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { PageHeader } from "@/components/crm/PageHeader";
import { EmptyState } from "@/components/crm/EmptyState";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";

const errorText = e => e.response?.data?.message || "Could not complete this request.";
const dateText = v => v ? new Date(v).toLocaleString() : "—";
export function SalesManagerAssignmentsPage() {
  const [tab, setTab] = useState("approved");
  const [executives, setExecutives] = useState([]);
  const [approved, setApproved] = useState([]);
  const [opportunities, setOpportunities] = useState([]);
  const [reopenRequests, setReopenRequests] = useState([]);
  const [selections, setSelections] = useState({});
  const [histories, setHistories] = useState({});
  const [error, setError] = useState("");
  const [busyId, setBusyId] = useState("");

  async function load() {
    try {
      const [aes, sqls, assigned, reopen] = await Promise.all([
        api.get("/sales-manager/account-executives"),
        api.get("/sales-manager/sql-qualifications/approved-for-assignment", { params: { size: 100 } }),
        api.get("/sales-manager/opportunities", { params: { size: 100 } }),
        api.get("/sales-manager/opportunity-reopen-requests"),
      ]);
      setExecutives(Array.isArray(aes.data) ? aes.data : []); setApproved(sqls.data.content || []); setOpportunities(assigned.data.content || []); setReopenRequests(reopen.data || []); setError("");
    } catch (e) { setError(errorText(e)); }
  }
  useEffect(() => { load(); }, []);

  async function assign(qualificationId) {
    const aeId = selections[qualificationId];
    if (!aeId) { setError("Choose an active AE before assigning this SQL."); return; }
    setBusyId(qualificationId); setError("");
    try { await api.post(`/sales-manager/sql-qualifications/${qualificationId}/assign`, { aeId }); await load(); }
    catch (e) { setError(errorText(e)); }
    finally { setBusyId(""); }
  }

  async function reassign(opportunityId) {
    const aeId = selections[opportunityId];
    if (!aeId) { setError("Choose an active AE before reassigning this opportunity."); return; }
    setBusyId(opportunityId); setError("");
    try { await api.put(`/sales-manager/opportunities/${opportunityId}/assignment`, { aeId }); await load(); await toggleHistory(opportunityId, true); }
    catch (e) { setError(errorText(e)); }
    finally { setBusyId(""); }
  }

  async function toggleHistory(id, force = false) {
    if (histories[id] && !force) { setHistories(current => ({ ...current, [id]: null })); return; }
    try { const result = await api.get(`/sales-manager/opportunities/${id}/assignment-history`); setHistories(current => ({ ...current, [id]: result.data })); }
    catch (e) { setError(errorText(e)); }
  }

  async function decideReopen(request, approved) {
    const reason = approved ? "" : window.prompt("Reason for rejecting this reopen request:");
    if (!approved && !reason?.trim()) return;
    setBusyId(request.id); setError("");
    try { await api.post(`/sales-manager/opportunity-reopen-requests/${request.id}/decision`, { approved, reason }); await load(); }
    catch (e) { setError(errorText(e)); }
    finally { setBusyId(""); }
  }

  return <>
    <PageHeader title="AE assignments" description="Assign approved SQLs to active AEs and review assignment history." breadcrumb={["Workspace", <Link key="manager" to="/sales-manager">Sales Manager</Link>, "AE assignments"]}/>
    {error && <p role="alert" className="mb-4 rounded-md bg-rose-50 p-3 text-sm text-rose-700">{error}</p>}
    <div className="mb-4 flex flex-wrap gap-2"><Button variant={tab === "approved" ? "default" : "outline"} onClick={() => setTab("approved")}>Approved SQLs ({approved.length})</Button><Button variant={tab === "opportunities" ? "default" : "outline"} onClick={() => setTab("opportunities")}>Assigned opportunities ({opportunities.length})</Button><Button variant={tab === "reopen" ? "default" : "outline"} onClick={() => setTab("reopen")}>Reopen requests ({reopenRequests.length})</Button></div>
    {tab === "approved" ? <section className="overflow-x-auto rounded-xl border bg-white">
      {!approved.length ? <EmptyState title="No approved SQLs awaiting assignment" description="Approved qualifications from BDRs will appear here."/> : <table className="w-full min-w-[1000px] text-left text-sm"><thead className="bg-slate-50 text-xs text-slate-500"><tr>{["Approved","Lead","Organisation","Contact","BDR","Value","Assign to",""].map(x=><th key={x} className="px-4 py-3 font-medium">{x}</th>)}</tr></thead><tbody>{approved.map(q=><tr key={q.id} className="border-t"><td className="px-4 py-3">{dateText(q.reviewedAt)}</td><td className="px-4 py-3 font-medium">{q.leadTitle}</td><td className="px-4 py-3">{q.organisationName}</td><td className="px-4 py-3">{q.primaryContactName}</td><td className="px-4 py-3">{q.createdByName}</td><td className="px-4 py-3">{money(q.estimatedDealValue)}</td><td className="px-4 py-3"><AeSelect executives={executives} value={selections[q.id]||""} onChange={value=>setSelections(v=>({...v,[q.id]:value}))}/></td><td className="px-4 py-3"><Button size="sm" disabled={busyId===q.id||!executives.length} onClick={()=>assign(q.id)}>{busyId===q.id?"Assigning…":"Assign"}</Button></td></tr>)}</tbody></table>}
    </section> : tab === "opportunities" ? <section className="space-y-3">{!opportunities.length ? <div className="rounded-xl border bg-white"><EmptyState title="No opportunities assigned yet" description="Assign an approved SQL to create the opportunity record."/></div> : opportunities.map(o=><article key={o.id} className="rounded-xl border bg-white p-4 sm:p-5"><div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-center"><div className="grid flex-1 gap-2 sm:grid-cols-2 lg:grid-cols-4"><Data label="Lead" value={o.leadTitle}/><Data label="Organisation" value={o.organisationName}/><Data label="Assigned AE" value={o.assignedAeName}/><Data label="Status / value" value={`${o.status} · ${money(o.amount, o.currencyCode)}`}/></div><div className="flex flex-wrap items-center gap-2"><AeSelect executives={executives} value={selections[o.id]||""} onChange={value=>setSelections(v=>({...v,[o.id]:value}))}/><Button size="sm" variant="outline" disabled={!o.active||busyId===o.id} onClick={()=>reassign(o.id)}>Reassign</Button><Button size="sm" variant="ghost" onClick={()=>toggleHistory(o.id)}>{histories[o.id]?"Hide history":"History"}</Button></div></div>{histories[o.id]&&<ol className="mt-4 space-y-2 border-l pl-4">{histories[o.id].map(h=><li key={h.id} className="text-xs text-slate-600"><strong>{h.action}</strong> · {h.previousAeName?`${h.previousAeName} → `:""}{h.assignedAeName} · by {h.assignedByName} · {dateText(h.createdAt)}</li>)}</ol>}</article>)}</section> : <section className="space-y-3">{!reopenRequests.length ? <div className="rounded-xl border bg-white"><EmptyState title="No pending reopen requests" description="AE requests to reopen closed opportunities will appear here."/></div> : reopenRequests.map(r=><article key={r.id} className="rounded-xl border bg-white p-4"><div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-start"><div><h2 className="font-semibold">{r.leadTitle} · {r.organisationName}</h2><p className="mt-1 text-sm text-slate-600">Assigned AE: {r.aeName}</p><p className="mt-2 text-sm">{r.reason}</p><p className="mt-1 text-xs text-slate-500">Requested {dateText(r.createdAt)}</p></div><div className="flex gap-2"><Button size="sm" disabled={busyId===r.id} onClick={()=>decideReopen(r,true)}>Approve reopen</Button><Button size="sm" variant="outline" disabled={busyId===r.id} onClick={()=>decideReopen(r,false)}>Reject</Button></div></div></article>)}</section>}
    <p className="mt-4 text-xs text-slate-500">Approved SQLs from all BDRs are available here. Any active AE can receive an assignment; reassignment is available while an opportunity is active.</p>
  </>;
}

function AeSelect({executives,value,onChange}) { return <select aria-label="Account executive" value={value} onChange={e=>onChange(e.target.value)} className="h-9 min-w-44 rounded-md border border-slate-200 bg-white px-2 text-sm"><option value="">Select AE</option>{executives.map(a=><option value={a.id} key={a.id}>{a.fullName}</option>)}</select>; }
function Data({label,value}) { return <div><p className="text-[11px] font-medium uppercase tracking-wide text-slate-400">{label}</p><p className="mt-1 text-sm font-medium text-slate-800">{value||"—"}</p></div>; }
function money(value,currency="INR") { return value == null ? "—" : new Intl.NumberFormat("en-IN",{style:"currency",currency,maximumFractionDigits:0}).format(Number(value)); }
