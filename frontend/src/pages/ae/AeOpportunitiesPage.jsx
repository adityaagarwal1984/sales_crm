import { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { EmptyState } from "@/components/crm/EmptyState";
import { PageHeader } from "@/components/crm/PageHeader";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";

const money=(value,currency="INR")=>new Intl.NumberFormat("en-IN",{style:"currency",currency,maximumFractionDigits:0}).format(Number(value||0));
const pages={
  opportunities:{title:"My opportunities",description:"Manage your assigned pipeline and advance opportunities through L1, L2, and L3.",empty:"No opportunities assigned",emptyDescription:"Approved SQLs assigned to you will appear here."},
  meetings:{title:"Meetings",description:"Open an assigned opportunity to log completed meetings, participants, outcomes, and follow-ups.",empty:"No active opportunities for meetings",emptyDescription:"Active assignments will appear here so you can open meeting history."},
  stakeholders:{title:"Stakeholders",description:"Open an opportunity to manage the contacts involved in the customer decision.",empty:"No opportunities found",emptyDescription:"Assigned opportunities will appear here for stakeholder management."},
  handovers:{title:"Implementation handovers",description:"Review won deals and their implementation handover details.",empty:"No handovers yet",emptyDescription:"A handover is created when an opportunity is closed as Won."},
};

export function AeOpportunitiesPage() {
  const location=useLocation();
  const view=location.pathname.split("/")[2]||"opportunities";
  const page=pages[view]||pages.opportunities;
  const [rows,setRows]=useState([]); const [error,setError]=useState(""); const [loading,setLoading]=useState(true);
  const [busyId,setBusyId]=useState(""); const [skipId,setSkipId]=useState(""); const [skipReason,setSkipReason]=useState("");
  async function load(){setLoading(true);try{const r=await api.get("/ae/opportunities",{params:{size:100}});setRows(r.data.content||[]);setError("");}catch(e){setError(e.response?.data?.message||"Could not load opportunities.");}finally{setLoading(false);}}
  useEffect(()=>{load();},[]);
  const visibleRows=view==="handovers"?rows.filter(o=>o.status==="WON"):view==="meetings"?rows.filter(o=>o.active):rows;
  async function moveStage(opportunity,stage,reason=null){setBusyId(opportunity.id);setError("");try{await api.patch(`/ae/opportunities/${opportunity.id}/stage`,{stage,reason});await load();setSkipId("");setSkipReason("");}catch(e){setError(e.response?.data?.message||"Could not update the opportunity stage.");}finally{setBusyId("");}}
  const detailHref=(id)=>`/ae/opportunities/${id}${view==="meetings"?"#meetings":view==="stakeholders"?"#stakeholders":view==="handovers"?"#handovers":""}`;
  const cta=view==="meetings"?"Open meeting workspace":view==="stakeholders"?"Open stakeholders":view==="handovers"?"Review handover":"Open opportunity";
  return <>
    <PageHeader title={page.title} description={page.description} breadcrumb={["Workspace",<Link key="ae" to="/ae">Account Executive</Link>,page.title]}/>
    {error&&<p role="alert" className="mb-4 rounded-md bg-rose-50 p-3 text-sm text-rose-700">{error}</p>}
    {loading?<p className="text-sm text-slate-500">Loading your opportunities…</p>:!visibleRows.length?<div className="rounded-xl border bg-white"><EmptyState title={page.empty} description={page.emptyDescription}/></div>:<section className="space-y-3">{visibleRows.map(o=><article key={o.id} className="rounded-xl border bg-white p-5">
      <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-start"><div><div className="flex flex-wrap items-center gap-2"><span className="rounded-full bg-blue-50 px-2.5 py-1 text-xs font-semibold text-blue-700">{o.status}</span><span className="text-xs text-slate-500">{money(o.amount,o.currencyCode)}</span></div><h2 className="mt-2 text-lg font-semibold text-slate-900">{o.leadTitle}</h2><p className="mt-1 text-sm text-slate-500">{o.organisationName} · Primary contact: {o.primaryContactName}</p></div><Link to={detailHref(o.id)}><Button variant="outline">{cta}</Button></Link></div>
      {view==="opportunities"&&o.active&&<div className="mt-4 flex flex-wrap items-center gap-2 border-t pt-4"><span className="mr-1 text-xs font-medium text-slate-500">Pipeline stage</span>{o.status==="L1"&&<><Button size="sm" disabled={busyId===o.id} onClick={()=>moveStage(o,"L2")}>{busyId===o.id?"Saving…":"Move to L2"}</Button>{skipId===o.id?<div className="flex min-w-full flex-col gap-2 sm:min-w-0 sm:flex-row"><input aria-label={`Reason for skipping L2 for ${o.leadTitle}`} value={skipReason} onChange={e=>setSkipReason(e.target.value)} maxLength="2000" placeholder="Reason for skipping L2" className="h-9 min-w-64 flex-1 rounded-md border px-3 text-sm"/><Button size="sm" variant="outline" disabled={busyId===o.id||!skipReason.trim()} onClick={()=>moveStage(o,"L3",skipReason.trim())}>Confirm L3</Button><Button size="sm" variant="ghost" onClick={()=>{setSkipId("");setSkipReason("");}}>Cancel</Button></div>:<Button size="sm" variant="outline" disabled={busyId===o.id} onClick={()=>{setSkipId(o.id);setSkipReason("");}}>Skip to L3</Button>}</>}{o.status==="L2"&&<Button size="sm" disabled={busyId===o.id} onClick={()=>moveStage(o,"L3")}>{busyId===o.id?"Saving…":"Move to L3"}</Button>}{o.status==="L3"&&<span className="text-sm text-slate-600">At L3 · Open the opportunity to record Won or Lost.</span>}</div>}
      {view==="meetings"&&<p className="mt-4 border-t pt-3 text-xs text-slate-500">Current stage: {o.status}. Meeting type and pipeline level are recorded separately.</p>}
    </article>)}</section>}
  </>;
}
