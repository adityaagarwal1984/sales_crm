import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Building2, ExternalLink, Plus } from "lucide-react";
import { PageHeader } from "@/components/crm/PageHeader";
import { SearchInput } from "@/components/crm/SearchInput";
import { EmptyState } from "@/components/crm/EmptyState";
import { Pagination } from "@/components/crm/Pagination";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { api } from "@/lib/api";
import { OrganisationForm } from "@/components/organisation/OrganisationForm";
import { useAuth } from "@/auth/AuthContext";

export function OrganisationListPage(){
 const {user}=useAuth();const canManage=user?.role==="BDR"||user?.role==="ADMIN";
 const [data,setData]=useState({content:[],totalElements:0,totalPages:0});const [query,setQuery]=useState("");const [industry,setIndustry]=useState("");const [companySize,setCompanySize]=useState("");const [leadSource,setLeadSource]=useState("");const [page,setPage]=useState(0);const [loading,setLoading]=useState(true);const [error,setError]=useState("");const [formOpen,setFormOpen]=useState(false);
 const params={search:query||undefined,industry:industry||undefined,companySize:companySize||undefined,leadSource:leadSource||undefined};
 useEffect(()=>{let alive=true;setLoading(true);const timer=setTimeout(()=>api.get("/organisations",{params:{...params,page,size:10}}).then(r=>{if(alive){setData(r.data);setError("");}}).catch(e=>{if(alive)setError(e.response?.data?.message||"Could not load organisations.");}).finally(()=>alive&&setLoading(false)),250);return()=>{alive=false;clearTimeout(timer);};},[query,industry,companySize,leadSource,page]);
 async function create(payload){await api.post("/organisations",payload);setPage(0);const r=await api.get("/organisations",{params:{...params,page:0,size:10}});setData(r.data);}
 return <><PageHeader title="Organisations" description="Shared company directory. BDRs and Admins can maintain records." breadcrumb={["Workspace","Organisations"]} action={canManage&&<Button onClick={()=>setFormOpen(true)}><Plus className="size-4"/>Add organisation</Button>}/>
   <section className="rounded-xl border border-slate-200 bg-white shadow-card"><div className="flex flex-col gap-3 border-b border-slate-100 p-4 sm:flex-row sm:flex-wrap"><SearchInput value={query} onChange={v=>{setQuery(v);setPage(0);}} label="Search organisations" placeholder="Name, website, industry…" className="sm:w-80"/><Input aria-label="Filter by industry" placeholder="Industry" value={industry} onChange={e=>{setIndustry(e.target.value);setPage(0);}} className="sm:w-40"/><Input aria-label="Filter by company size" placeholder="Company size" value={companySize} onChange={e=>{setCompanySize(e.target.value);setPage(0);}} className="sm:w-40"/><Input aria-label="Filter by lead source" placeholder="Lead source" value={leadSource} onChange={e=>{setLeadSource(e.target.value);setPage(0);}} className="sm:w-40"/></div>
    {error?<p role="alert" className="p-8 text-center text-sm text-rose-600">{error}</p>:loading?<div className="p-8 text-center text-sm text-slate-500">Loading organisations…</div>:data.content.length===0?<EmptyState title="No organisations yet" description="Add your first organisation or adjust the search and filters."/>:<div className="overflow-x-auto"><table className="w-full min-w-[720px] text-left text-sm"><thead><tr className="bg-slate-50 text-xs text-slate-500"><th className="px-5 py-3 font-medium">Organisation</th><th className="px-5 py-3 font-medium">Industry</th><th className="px-5 py-3 font-medium">Location</th><th className="px-5 py-3 font-medium">Contacts</th><th className="px-5 py-3 font-medium">Lead source</th><th className="px-5 py-3"/></tr></thead><tbody className="divide-y divide-slate-100">{data.content.map(o=><tr key={o.id} className="hover:bg-slate-50/70"><td className="px-5 py-4"><Link className="font-medium text-slate-900 hover:text-blue-700" to={`/organisations/${o.id}`}>{o.name}</Link><p className="mt-1 text-xs text-slate-500">{o.website||"No website"}{o.companySize?` · ${o.companySize}`:""}</p></td><td className="px-5 py-4 text-slate-600">{o.industry||"—"}</td><td className="px-5 py-4 text-slate-600">{o.location||"—"}</td><td className="px-5 py-4 text-slate-600">{o.contactCount}</td><td className="px-5 py-4 text-slate-600">{o.leadSource||"—"}</td><td className="px-5 py-4"><Link aria-label={`Open ${o.name}`} to={`/organisations/${o.id}`} className="text-slate-400 hover:text-blue-700"><ExternalLink className="size-4"/></Link></td></tr>)}</tbody></table></div>}
    <div className="border-t border-slate-100 px-5 py-3"><Pagination page={page+1} pageCount={data.totalPages} itemCount={data.content.length} onPageChange={v=>setPage(v-1)}/><p className="mt-1 text-xs text-slate-400">{data.totalElements} organisations</p></div>
   </section>{canManage&&<OrganisationForm open={formOpen} onOpenChange={setFormOpen} onSave={create}/>}</>;
}
