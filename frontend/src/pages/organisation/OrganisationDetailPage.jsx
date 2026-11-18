import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { ArrowLeft, Building2, Mail, MapPin, Pencil, Plus, UserRound } from "lucide-react";
import { PageHeader } from "@/components/crm/PageHeader";
import { EmptyState } from "@/components/crm/EmptyState";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { api } from "@/lib/api";
import { OrganisationForm } from "@/components/organisation/OrganisationForm";
import { ContactForm } from "@/components/organisation/ContactForm";
import { useAuth } from "@/auth/AuthContext";

export function OrganisationDetailPage(){const {id}=useParams();const navigate=useNavigate();const {user}=useAuth();const canManage=user?.role==="BDR"||user?.role==="ADMIN";const [org,setOrg]=useState(null);const [error,setError]=useState("");const [editOpen,setEditOpen]=useState(false);const [contactOpen,setContactOpen]=useState(false);
 async function load(){try{const r=await api.get(`/organisations/${id}`);setOrg(r.data);setError("");}catch(e){setError(e.response?.data?.message||"Could not load organisation.");}}
 useEffect(()=>{load();},[id]);
 async function update(payload){await api.put(`/organisations/${id}`,payload);await load();}
 async function addContact(payload){await api.post(`/organisations/${id}/contacts`,payload);await load();}
 return <>{error?<div role="alert" className="rounded-lg border border-rose-200 bg-rose-50 p-4 text-sm text-rose-700">{error}<div><Button variant="ghost" className="mt-2" onClick={()=>navigate("/organisations")}><ArrowLeft className="size-4"/>Back to organisations</Button></div></div>:org&&<>
   <PageHeader title={org.name} description={org.website||"Organisation profile"} breadcrumb={["Workspace","Organisations",org.name]} action={canManage&&<div className="flex gap-2"><Button variant="outline" onClick={()=>setEditOpen(true)}><Pencil className="size-4"/>Edit</Button><Button onClick={()=>setContactOpen(true)}><Plus className="size-4"/>Add contact</Button></div>}/>
   <div className="grid gap-5 xl:grid-cols-[minmax(0,1fr)_320px]"><div className="space-y-5"><Card><CardHeader><CardTitle>Company profile</CardTitle></CardHeader><CardContent><dl className="grid gap-x-8 gap-y-5 sm:grid-cols-2">{[["Industry",org.industry],["Company size",org.companySize],["Location",org.location],["Lead source",org.leadSource],["Address",org.address],["Description",org.description]].map(([label,value])=><div key={label}><dt className="text-xs font-medium uppercase tracking-wide text-slate-400">{label}</dt><dd className="mt-1 whitespace-pre-line text-sm text-slate-700">{value||"—"}</dd></div>)}</dl></CardContent></Card>
    <Card><CardHeader className="flex-row items-center justify-between"><div><CardTitle>Contacts</CardTitle><p className="mt-1 text-xs text-slate-500">People linked to this organisation</p></div>{canManage&&<Button size="sm" variant="outline" onClick={()=>setContactOpen(true)}><Plus className="size-4"/>Add contact</Button>}</CardHeader><CardContent className="p-0">{!org.contacts.length?<EmptyState title="No contacts yet" description="Add the people you are connecting with at this organisation."/>:<div className="divide-y divide-slate-100">{org.contacts.map(c=><Link key={c.id} to={`/contacts/${c.id}`} className="flex items-center justify-between gap-4 px-5 py-4 hover:bg-slate-50"><div className="flex min-w-0 items-center gap-3"><span className="grid size-9 shrink-0 place-items-center rounded-full bg-blue-50 text-blue-700"><UserRound className="size-4"/></span><div className="min-w-0"><p className="truncate text-sm font-medium text-slate-900">{c.name}</p><p className="mt-0.5 truncate text-xs text-slate-500">{[c.designation,c.department].filter(Boolean).join(" · ")||"No role details"}</p></div></div><span className="hidden items-center gap-1 text-xs text-slate-500 sm:flex"><Mail className="size-3.5"/>{c.email||"No email"}</span></Link>)}</div>}</CardContent></Card>
   </div><Card className="h-fit"><CardHeader><CardTitle>Record details</CardTitle></CardHeader><CardContent className="space-y-4 text-sm"><div className="flex gap-3"><Building2 className="mt-0.5 size-4 text-slate-400"/><span>{org.website?<a className="text-blue-700 hover:underline" href={org.website} target="_blank" rel="noreferrer">{org.website}</a>:"No website"}</span></div><div className="flex gap-3"><MapPin className="mt-0.5 size-4 text-slate-400"/><span>{org.location||"Location not provided"}</span></div><div className="border-t border-slate-100 pt-3 text-xs text-slate-500">Created by {org.createdByName}<br/>{new Date(org.createdAt).toLocaleDateString()}</div></CardContent></Card></div>
   {canManage&&<><OrganisationForm open={editOpen} onOpenChange={setEditOpen} initialValue={org} onSave={update}/><ContactForm open={contactOpen} onOpenChange={setContactOpen} onSave={addContact}/></>}
  </>}</>;
}
