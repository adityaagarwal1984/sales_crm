import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ArrowLeft, ArrowRight, Building2, Check, ClipboardCheck, LineChart, LockKeyhole, ShieldCheck } from "lucide-react";
import { useAuth } from "@/auth/AuthContext";
import { roleNavigation } from "@/config/roleNavigation";

const highlights = [
  { icon: Building2, title: "Build shared context", text: "Keep organisations, contacts, and sales pursuits connected." },
  { icon: ClipboardCheck, title: "Make handoffs clear", text: "Carry qualification and decisions into the next step." },
  { icon: LineChart, title: "Move with visibility", text: "See progress across people, pipeline, and revenue." },
];

export function LoginPage() {
  const { login } = useAuth();
  const nav = useNavigate();
  const [email,setEmail] = useState("");
  const [password,setPassword] = useState("");
  const [error,setError] = useState("");
  const [busy,setBusy] = useState(false);

  async function submit(e) {
    e.preventDefault(); setBusy(true); setError("");
    try {
      const user = await login(email,password);
      nav(user.mustChangePassword?"/change-password":`/${roleNavigation[user.role]?.dashboard||"admin"}`,{replace:true});
    } catch {
      setError("Invalid email or password. Please try again.");
    } finally {
      setBusy(false);
    }
  }

  return <main className="login-screen min-h-screen bg-[#f6f8f6] lg:grid lg:grid-cols-[1.04fr_.96fr]">
    <section className="login-story relative isolate flex min-h-[420px] flex-col overflow-hidden bg-[#082f1b] px-6 py-6 text-white sm:px-10 sm:py-8 lg:min-h-screen lg:px-12 xl:px-16">
      <div className="login-grid absolute inset-0 opacity-30" aria-hidden="true"/>
      <div className="login-glow absolute -right-28 top-10 size-[28rem] rounded-full" aria-hidden="true"/>
      <div className="relative z-10 flex items-center justify-between"><Link to="/" className="inline-flex items-center gap-2.5 rounded-md focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#75d99b]" aria-label="Prosperr home"><span className="grid size-9 place-items-center rounded-xl bg-white p-1.5"><img src="/prosperr-mark.png" alt="" className="size-6 object-contain"/></span><span><strong className="block text-sm leading-5 text-white">Prosperr</strong><span className="block text-[9px] font-semibold uppercase tracking-[.16em] text-white/55">Sales CRM</span></span></Link><span className="hidden items-center gap-1.5 rounded-full border border-white/15 bg-white/[.06] px-3 py-1.5 text-[10px] font-semibold uppercase tracking-wide text-white/65 sm:inline-flex"><ShieldCheck className="size-3.5 text-[#75d99b]"/> Secure workspace</span></div>

      <div className="relative z-10 my-auto max-w-2xl py-12 lg:py-16">
        <p className="text-[10px] font-bold uppercase tracking-[.18em] text-[#9bdab0]">Your work, in one connected flow</p>
        <h1 className="mt-4 max-w-xl text-3xl font-semibold leading-[1.08] tracking-[-.045em] sm:text-4xl lg:text-5xl">Welcome back to the sales journey.</h1>
        <p className="mt-5 max-w-lg text-sm leading-6 text-white/65 sm:text-base sm:leading-7">Sign in to pick up where your team left off—from first contact to qualification, opportunity, and close.</p>
        <div className="relative mt-9 space-y-3">{highlights.map((item,index)=>{const Icon=item.icon;return <article key={item.title} className="login-highlight flex items-center gap-3.5 rounded-xl border border-white/10 bg-white/[.045] p-3.5 backdrop-blur-sm" style={{animationDelay:`${index*100}ms`}}><span className="grid size-9 shrink-0 place-items-center rounded-lg border border-white/10 bg-white/[.07] text-[#a9e3bc]"><Icon className="size-4"/></span><span><b className="block text-xs font-semibold text-white/90">{item.title}</b><small className="mt-0.5 block text-[11px] leading-4 text-white/50">{item.text}</small></span><Check className="ml-auto size-4 shrink-0 text-[#75d99b]"/></article>})}</div>
        <div className="mt-7 flex flex-wrap gap-2">{["BDR","SALES MANAGER","ACCOUNT EXECUTIVE","LEADERSHIP"].map((role,i)=><span key={role} className={`rounded-md border px-2.5 py-1.5 text-[9px] font-bold tracking-[.08em] ${i===0?"border-[#75d99b]/30 bg-[#75d99b]/10 text-[#a8e2bd]":"border-white/10 bg-white/[.04] text-white/50"}`}>{role}</span>)}</div>
      </div>
      <div className="relative z-10 hidden items-center justify-between border-t border-white/10 pt-4 text-[10px] text-white/40 lg:flex"><span>Prosperr Sales CRM</span><span className="inline-flex items-center gap-1.5"><LockKeyhole className="size-3"/> Role-based access</span></div>
    </section>

    <section className="relative flex min-h-[580px] items-center justify-center overflow-hidden px-5 py-12 sm:px-10 lg:min-h-screen lg:px-12 xl:px-16">
      <div className="login-form-glow absolute -right-36 -top-32 size-[28rem] rounded-full" aria-hidden="true"/>
      <div className="login-form-glow login-form-glow-lower absolute -bottom-48 -left-36 size-[24rem] rounded-full" aria-hidden="true"/>
      <div className="relative z-10 w-full max-w-[430px]">
        <Link to="/" className="mb-7 inline-flex items-center gap-1.5 text-xs font-medium text-slate-500 transition hover:text-[#0b3b24]"><ArrowLeft className="size-3.5"/> Back to home</Link>
        <form onSubmit={submit} className="login-card space-y-5 rounded-2xl border border-[#e1e9e2] bg-white p-6 shadow-[0_28px_80px_-44px_rgba(5,63,26,.35)] sm:p-8">
          <div><span className="grid size-11 place-items-center rounded-xl bg-[#edf5f0] text-[#0b3b24]"><LockKeyhole className="size-5"/></span><p className="mt-6 text-[10px] font-bold uppercase tracking-[.16em] text-[#398257]">Prosperr team workspace</p><h2 className="mt-2 text-2xl font-semibold tracking-[-.035em] text-[#092f1b]">Sign in to your account</h2><p className="mt-2 text-sm leading-5 text-slate-500">Use the work account provided by your administrator.</p></div>
          {error&&<p role="alert" className="rounded-lg border border-rose-200 bg-rose-50 px-3.5 py-3 text-sm text-rose-700">{error}</p>}
          <label htmlFor="work-email" className="block text-xs font-semibold text-slate-700">Work email<input id="work-email" className="mt-2 h-11 w-full rounded-lg border border-slate-200 bg-white px-3.5 text-sm font-normal text-slate-900 outline-none transition placeholder:text-slate-400 hover:border-slate-300 focus:border-[#5e9972] focus:ring-4 focus:ring-[#edf5f0]" type="email" autoComplete="username" placeholder="you@prosperr.io" required value={email} onChange={e=>setEmail(e.target.value)}/></label>
          <label htmlFor="work-password" className="block text-xs font-semibold text-slate-700">Password<input id="work-password" className="mt-2 h-11 w-full rounded-lg border border-slate-200 bg-white px-3.5 text-sm font-normal text-slate-900 outline-none transition placeholder:text-slate-400 hover:border-slate-300 focus:border-[#5e9972] focus:ring-4 focus:ring-[#edf5f0]" type="password" autoComplete="current-password" placeholder="Enter your password" required value={password} onChange={e=>setPassword(e.target.value)}/></label>
          <button type="submit" disabled={busy} className="group flex h-11 w-full items-center justify-center gap-2 rounded-lg bg-[#0b3b24] px-4 text-sm font-semibold text-white shadow-sm transition hover:-translate-y-0.5 hover:bg-[#053f1a] hover:shadow-md focus-visible:outline-none focus-visible:ring-4 focus-visible:ring-[#dcebe1] disabled:translate-y-0 disabled:cursor-wait disabled:opacity-65">{busy?"Signing in…":"Sign in"}{!busy&&<ArrowRight className="size-4 transition group-hover:translate-x-0.5"/>}</button>
          <div className="flex items-center justify-center gap-2 border-t border-slate-100 pt-4 text-[10px] text-slate-400"><ShieldCheck className="size-3.5 text-[#398257]"/> Your access follows your assigned CRM role</div>
        </form>
        <p className="mt-5 text-center text-[11px] text-slate-400">Need access? Contact your Prosperr administrator.</p>
        <p className="mt-8 text-center text-[10px] text-slate-400">© {new Date().getFullYear()} Prosperr · Sales CRM</p>
      </div>
    </section>
  </main>;
}
