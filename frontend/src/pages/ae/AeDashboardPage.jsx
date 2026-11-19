import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { PageHeader } from "@/components/crm/PageHeader";
import { StatCard } from "@/components/crm/StatCard";
import { Button } from "@/components/ui/button";
import { api } from "@/lib/api";

const initial = { assignedOpportunities:0,l1Meetings:0,l2Meetings:0,l3Meetings:0,pendingFollowUps:0,wonDeals:0,lostDeals:0,totalPipelineValue:0,revenueGenerated:0,currencyCode:"INR" };
const money = (v,c="INR") => new Intl.NumberFormat("en-IN",{style:"currency",currency:c,maximumFractionDigits:0}).format(Number(v||0));
export function AeDashboardPage() {
  const [data,setData]=useState(initial); const [error,setError]=useState("");
  useEffect(()=>{api.get("/ae/opportunities/dashboard").then(r=>setData(r.data)).catch(e=>setError(e.response?.data?.message||"Could not load your AE dashboard."));},[]);
  const cards=[
    ["Assigned Opportunities",data.assignedOpportunities,"pipeline","blue"],["L1 Meetings",data.l1Meetings,"calendar","blue"],
    ["L2 Meetings",data.l2Meetings,"calendar","violet"],["L3 Meetings",data.l3Meetings,"calendar","violet"],
    ["Pending Follow-ups",data.pendingFollowUps,"target","amber"],["Won Deals",data.wonDeals,"handover","green"],
    ["Lost Deals",data.lostDeals,"activity","amber"],["Total Pipeline Value",money(data.totalPipelineValue,data.currencyCode),"pipeline","blue"],
    ["Revenue Generated",money(data.revenueGenerated,data.currencyCode),"revenue","green"],
  ];
  return <><PageHeader title="Account Executive dashboard" description="Your assigned opportunity pipeline, customer meetings, and revenue." breadcrumb={["Workspace","Account Executive","Dashboard"]}/>{error&&<p role="alert" className="mb-4 text-sm text-rose-700">{error}</p>}<section aria-label="AE performance metrics" className="grid gap-3 sm:grid-cols-2 xl:grid-cols-3">{cards.map(([label,value,icon,tone])=><StatCard key={label} label={label} value={value} icon={icon} tone={tone} change="Assigned opportunity records"/>)}</section><section className="mt-5 rounded-xl border bg-white p-5"><h2 className="font-semibold text-slate-900">My opportunities</h2><p className="mt-1 text-sm text-slate-500">Review qualification details, organisation contacts, stakeholders, meeting history, and stage progression.</p><Link to="/ae/opportunities" className="mt-4 inline-flex"><Button>Open opportunities</Button></Link></section></>;
}
