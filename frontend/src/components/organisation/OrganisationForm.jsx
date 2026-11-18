import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { FormField } from "@/components/crm/FormField";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";

const blank={name:"",website:"",industry:"",companySize:"",location:"",address:"",leadSource:"",description:""};
export function OrganisationForm({open,onOpenChange,initialValue,onSave}){
  const [form,setForm]=useState(blank);const [error,setError]=useState("");const [saving,setSaving]=useState(false);
  useEffect(()=>{setForm(initialValue?Object.fromEntries(Object.keys(blank).map(k=>[k,initialValue[k]||""])):blank);setError("");},[initialValue,open]);
  function change(event){setForm(current=>({...current,[event.target.name]:event.target.value}));}
  async function submit(event){event.preventDefault();setSaving(true);setError("");try{await onSave(Object.fromEntries(Object.entries(form).map(([k,v])=>[k,v.trim()||null])));onOpenChange(false);}catch(e){setError(e.response?.data?.message||"Could not save this organisation.");}finally{setSaving(false);}}
  return <Dialog open={open} onOpenChange={onOpenChange}><DialogContent className="max-h-[90vh] overflow-y-auto sm:max-w-2xl"><DialogHeader><DialogTitle>{initialValue?"Edit organisation":"Add organisation"}</DialogTitle><DialogDescription>Keep company information together with its contacts.</DialogDescription></DialogHeader><form onSubmit={submit} className="grid gap-4 sm:grid-cols-2">
    <FormField id="org-name" name="name" label="Organisation name" required maxLength={180} value={form.name} onChange={change}/><FormField id="org-website" name="website" label="Website" type="url" placeholder="https://example.com" value={form.website} onChange={change}/><FormField id="org-industry" name="industry" label="Industry" maxLength={120} value={form.industry} onChange={change}/><FormField id="org-size" name="companySize" label="Company size" placeholder="e.g. 51–200 employees" maxLength={80} value={form.companySize} onChange={change}/><FormField id="org-location" name="location" label="Location" maxLength={180} value={form.location} onChange={change}/><FormField id="org-source" name="leadSource" label="Lead source" maxLength={120} value={form.leadSource} onChange={change}/><div className="sm:col-span-2"><FormField id="org-address" name="address" label="Address" as="textarea" maxLength={10000} value={form.address} onChange={change}/></div><div className="sm:col-span-2"><FormField id="org-description" name="description" label="Description" as="textarea" maxLength={10000} value={form.description} onChange={change}/></div>
    {error&&<p role="alert" className="text-sm text-rose-600 sm:col-span-2">{error}</p>}<DialogFooter className="sm:col-span-2"><Button variant="outline" onClick={()=>onOpenChange(false)}>Cancel</Button><Button type="submit" disabled={saving}>{saving?"Saving…":"Save organisation"}</Button></DialogFooter>
  </form></DialogContent></Dialog>;
}
