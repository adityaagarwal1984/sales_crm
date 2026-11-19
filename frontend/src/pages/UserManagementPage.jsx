import { useEffect, useMemo, useState } from "react";
import { api } from "@/lib/api";

const roles = ["ADMIN", "BDR", "SALES_MANAGER", "AE", "LEADERSHIP"];
const requiresManager = role => role === "BDR" || role === "AE";

export function UserManagementPage() {
  const [users, setUsers] = useState([]);
  const [role, setRole] = useState("ADMIN");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const managers = useMemo(() => users.filter(user => user.role === "SALES_MANAGER" && user.active), [users]);

  async function load() {
    try { const response = await api.get("/admin/users"); setUsers(response.data); setError(""); }
    catch { setError("Could not load users. Confirm that you are signed in as an administrator."); }
  }
  useEffect(() => { load(); }, []);

  async function create(event) {
    event.preventDefault();
    const values = new FormData(event.currentTarget);
    setBusy(true);
    try {
      await api.post("/admin/users", {
        fullName: values.get("fullName"), email: values.get("email"), role,
        managerId: requiresManager(role) ? values.get("managerId") || null : null,
        temporaryPassword: values.get("temporaryPassword"),
      });
      event.currentTarget.reset(); setRole("ADMIN"); await load();
    } catch (e) { setError(e.response?.data?.message || "User could not be created. Check the required fields and unique email."); }
    finally { setBusy(false); }
  }

  async function toggle(user) {
    try { await api.patch(`/admin/users/${user.id}/status`, { active: !user.active }); await load(); }
    catch { setError("Status update failed. The last active administrator cannot be deactivated."); }
  }

  async function edit(user) {
    const fullName = window.prompt("Full name:", user.fullName); if (fullName === null) return;
    const email = window.prompt("Work email:", user.email); if (email === null) return;
    const nextRole = window.prompt(`Role (${roles.join(", ")}):`, user.role); if (nextRole === null) return;
    if (!roles.includes(nextRole)) { setError("Choose one of the listed roles."); return; }
    try { await api.put(`/admin/users/${user.id}`, { fullName, email, role: nextRole, managerId: requiresManager(nextRole) ? user.managerId : null }); await load(); }
    catch (e) { setError(e.response?.data?.message || "User update failed. Check the email, role and team assignment."); }
  }

  async function setManager(user, managerId) {
    try { await api.put(`/admin/users/${user.id}`, { fullName: user.fullName, email: user.email, role: user.role, managerId: managerId || null }); await load(); }
    catch (e) { setError(e.response?.data?.message || "Team assignment could not be updated."); }
  }

  async function reset(user) {
    const temporaryPassword = window.prompt("Enter a temporary password (at least 12 characters):"); if (!temporaryPassword) return;
    try { await api.post(`/admin/users/${user.id}/password-reset`, { temporaryPassword }); window.alert("Temporary password set; user must change it at next sign in."); await load(); }
    catch { setError("Password reset failed. Use at least 12 characters."); }
  }

  return <main className="space-y-6">
    <header><p className="text-xs text-slate-500">Workspace / Administrator / Users & roles</p><h1 className="mt-2 text-2xl font-semibold">Users & roles</h1><p className="mt-1 text-sm text-slate-500">Create accounts and manage role access. Every Sales Manager can view all BDR and AE accounts.</p></header>
    {error && <p role="alert" className="rounded-md bg-red-50 p-3 text-sm text-red-700">{error}</p>}
    <form onSubmit={create} className="grid gap-3 rounded-xl border bg-white p-5 md:grid-cols-6">
      <input name="fullName" required maxLength="120" placeholder="Full name" className="rounded-md border px-3 py-2"/>
      <input name="email" type="email" required placeholder="Work email" className="rounded-md border px-3 py-2"/>
      <select name="role" value={role} onChange={e => setRole(e.target.value)} className="rounded-md border px-3 py-2">{roles.map(item => <option key={item}>{item}</option>)}</select>
      <select name="managerId" disabled={!requiresManager(role)} className="rounded-md border px-3 py-2 disabled:bg-slate-50"><option value="">Optional manager link</option>{managers.map(manager => <option value={manager.id} key={manager.id}>{manager.fullName}</option>)}</select>
      <input name="temporaryPassword" type="password" minLength="12" maxLength="72" required placeholder="Temporary password" className="rounded-md border px-3 py-2"/>
      <button disabled={busy} className="rounded-md bg-blue-600 px-3 py-2 text-white disabled:opacity-50">Create user</button>
    </form>
    {!managers.length && <p className="-mt-4 text-xs text-slate-500">A manager link is optional; all Sales Managers can access BDR and AE records.</p>}
    <div className="overflow-x-auto rounded-xl border bg-white"><table className="w-full min-w-[900px] text-left text-sm"><thead className="bg-slate-50 text-xs text-slate-500"><tr>{["Name","Email","Role","Sales Manager","Status","Actions"].map(label=><th key={label} className="px-4 py-3">{label}</th>)}</tr></thead><tbody>{users.map(user=><tr key={user.id} className="border-t"><td className="px-4 py-3 font-medium">{user.fullName}</td><td className="px-4 py-3">{user.email}</td><td className="px-4 py-3">{user.role}</td><td className="px-4 py-3">{requiresManager(user.role) ? <select aria-label={`Sales Manager for ${user.fullName}`} value={user.managerId || ""} onChange={e=>setManager(user,e.target.value)} className="h-8 rounded-md border px-2"><option value="">Unassigned</option>{managers.map(manager=><option key={manager.id} value={manager.id}>{manager.fullName}</option>)}</select> : "—"}</td><td className="px-4 py-3">{user.active ? "Active" : "Inactive"}</td><td className="space-x-3 whitespace-nowrap px-4 py-3"><button onClick={()=>edit(user)} className="text-blue-700">Edit</button><button onClick={()=>toggle(user)} className="text-blue-700">{user.active ? "Deactivate" : "Activate"}</button><button onClick={()=>reset(user)} className="text-blue-700">Reset password</button></td></tr>)}{!users.length&&<tr><td colSpan="6" className="p-8 text-center text-slate-500">No users found.</td></tr>}</tbody></table></div>
  </main>;
}
