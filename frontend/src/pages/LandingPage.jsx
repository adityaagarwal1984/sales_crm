import { Link } from "react-router-dom";
import { ArrowDown, ArrowRight, ArrowUpRight, BarChart3, Building2, Check, CircleCheck, ClipboardCheck, Handshake, LineChart, PhoneCall, ShieldCheck, UsersRound } from "lucide-react";
import { Button } from "@/components/ui/button";

const flow = [
  { n: "01", title: "Discover", role: "BDR", text: "Organisations, people, and sales pursuits", icon: Building2 },
  { n: "02", title: "Qualify", role: "BDR + Manager", text: "Calls, follow-ups, and SQL review", icon: PhoneCall },
  { n: "03", title: "Assign", role: "Sales Manager", text: "Approved opportunities routed to an AE", icon: ClipboardCheck },
  { n: "04", title: "Progress", role: "Account Executive", text: "Stakeholders and L1 → L2 → L3 meetings", icon: UsersRound },
  { n: "05", title: "Close", role: "AE + Leadership", text: "Won or Lost, handover, and analytics", icon: BarChart3 },
];

const ownership = [
  { role: "Business development", label: "BDR", title: "Start with a real conversation.", text: "Find organisations, build contact context, log calls, and submit qualified sales pursuits.", icon: PhoneCall, tint: "bg-[#eff6f0] text-[#0b3b24]" },
  { role: "Sales management", label: "MANAGER", title: "Make the next handoff clear.", text: "Review SQLs, assign approved opportunities, and keep team progress visible.", icon: ClipboardCheck, tint: "bg-[#f2f5e9] text-[#536c19]" },
  { role: "Account executive", label: "AE", title: "Move every opportunity forward.", text: "Coordinate stakeholders, record meetings, progress stages, and close with a handover.", icon: Handshake, tint: "bg-[#edf5f5] text-[#17645c]" },
  { role: "Leadership", label: "LEADERSHIP", title: "See performance across the journey.", text: "Follow the funnel, compare departments, and understand revenue from one view.", icon: LineChart, tint: "bg-[#f2f1f7] text-[#514578]" },
];

export function LandingPage() {
  return <div className="min-h-screen overflow-hidden bg-[#f7f8f6] text-slate-900">
    <header className="relative z-20 border-b border-white/10 bg-[#082f1b] text-white">
      <div className="mx-auto flex h-[72px] max-w-7xl items-center justify-between px-5 sm:px-8 lg:px-10">
        <Brand light/>
        <nav aria-label="Landing page" className="flex items-center gap-2 sm:gap-5">
          <a href="#journey" className="hidden text-sm font-medium text-white/70 transition hover:text-white sm:inline">How it works</a>
          <a href="#teams" className="hidden text-sm font-medium text-white/70 transition hover:text-white md:inline">For every role</a>
          <Link to="/login"><Button className="h-10 rounded-lg border border-white/20 bg-white text-[#0b3b24] shadow-sm hover:bg-[#edf5f0]">Sign in <ArrowUpRight className="ml-1 size-4"/></Button></Link>
        </nav>
      </div>
    </header>

    <main>
      <section className="landing-hero relative isolate overflow-hidden bg-[#082f1b] text-white">
        <div className="landing-grid absolute inset-0 opacity-30" aria-hidden="true"/>
        <div className="landing-orb landing-orb-one absolute -right-32 -top-40 size-[34rem] rounded-full" aria-hidden="true"/>
        <div className="landing-orb landing-orb-two absolute -bottom-64 left-[22%] size-[36rem] rounded-full" aria-hidden="true"/>
        <div className="relative mx-auto grid max-w-7xl items-center gap-12 px-5 pb-20 pt-14 sm:px-8 sm:pb-24 sm:pt-20 lg:min-h-[650px] lg:grid-cols-[.92fr_1.08fr] lg:gap-16 lg:px-10 lg:py-20">
          <div className="landing-copy relative z-10 max-w-2xl">
            <div className="mb-6 inline-flex items-center gap-2.5 rounded-full border border-white/15 bg-white/[.06] px-3.5 py-2 text-[10px] font-semibold uppercase tracking-[.17em] text-[#c8ead3] shadow-[inset_0_1px_0_rgba(255,255,255,.05)]"><span className="relative flex size-2"><span className="absolute inline-flex size-full animate-ping rounded-full bg-[#6ee7a0] opacity-50"/><span className="relative inline-flex size-2 rounded-full bg-[#6ee7a0]"/></span> PROSPERR SALES CRM</div>
            <h1 className="max-w-[720px] text-[2.7rem] font-semibold leading-[1.04] tracking-[-.055em] sm:text-6xl lg:text-[4.35rem]">A clearer path from <span className="text-[#75d99b]">first touch</span> to closed deal.</h1>
            <p className="mt-6 max-w-xl text-base leading-7 text-white/70 sm:text-lg sm:leading-8">One connected workspace for the people who discover opportunities, qualify them, move them forward, and learn from every outcome.</p>
            <div className="mt-8 flex flex-wrap items-center gap-3">
              <Link to="/login"><Button className="h-12 rounded-lg bg-[#75d99b] px-6 text-sm font-semibold text-[#082f1b] shadow-[0_10px_30px_-12px_rgba(117,217,155,.55)] transition hover:-translate-y-0.5 hover:bg-[#91e5ae]">Enter your workspace <ArrowRight className="ml-1 size-4"/></Button></Link>
              <a href="#journey" className="group inline-flex h-12 items-center gap-2 rounded-lg px-4 text-sm font-semibold text-white/80 transition hover:bg-white/[.07] hover:text-white">Explore the workflow <ArrowDown className="size-4 transition group-hover:translate-y-0.5"/></a>
            </div>
            <div className="mt-9 flex flex-wrap gap-x-5 gap-y-2.5 border-t border-white/10 pt-5 text-xs text-white/60"><span className="inline-flex items-center gap-1.5"><CircleCheck className="size-3.5 text-[#75d99b]"/> Clear ownership</span><span className="inline-flex items-center gap-1.5"><CircleCheck className="size-3.5 text-[#75d99b]"/> Traceable decisions</span><span className="inline-flex items-center gap-1.5"><CircleCheck className="size-3.5 text-[#75d99b]"/> Shared customer context</span></div>
          </div>

          <div className="landing-visual relative mx-auto w-full max-w-[620px] lg:ml-auto">
            <div className="absolute -inset-6 rounded-[2rem] bg-[#57bf7c]/10 blur-2xl" aria-hidden="true"/>
            <div className="relative rounded-[1.35rem] border border-white/15 bg-[#f9fbf9] p-3 text-slate-900 shadow-[0_35px_100px_-45px_rgba(0,0,0,.7)] sm:rotate-[1deg] sm:p-4">
              <div className="flex items-center justify-between rounded-xl border border-slate-200/80 bg-white px-4 py-3"><div className="flex items-center gap-3"><span className="grid size-9 place-items-center rounded-lg bg-[#edf5f0] text-[#0b3b24]"><LineChart className="size-[18px]"/></span><div><p className="text-[10px] font-semibold uppercase tracking-[.13em] text-slate-400">Sales workspace</p><p className="mt-0.5 text-sm font-semibold text-slate-800">Opportunity journey</p></div></div><span className="hidden rounded-full border border-[#dcebe1] bg-[#f4f8f5] px-2.5 py-1 text-[10px] font-semibold text-[#287248] sm:inline-flex">OWNERSHIP AT EVERY STEP</span><span className="flex gap-1"><i className="size-1.5 rounded-full bg-slate-200"/><i className="size-1.5 rounded-full bg-slate-200"/><i className="size-1.5 rounded-full bg-slate-200"/></span></div>
              <div className="mt-3 grid gap-3 md:grid-cols-[.83fr_1.17fr]">
                <div className="rounded-xl bg-[#0b3b24] p-4 text-white sm:p-5"><p className="text-[10px] font-semibold uppercase tracking-[.14em] text-[#9bdab0]">The handoff</p><p className="mt-3 text-xl font-semibold leading-6 tracking-tight">One opportunity.<br/>A team behind it.</p><p className="mt-3 text-xs leading-5 text-white/65">Move context with the work, from first call through implementation.</p><div className="mt-6 flex -space-x-2"><span className="grid size-8 place-items-center rounded-full border-2 border-[#0b3b24] bg-[#cde9d6] text-[9px] font-bold text-[#0b3b24]">BDR</span><span className="grid size-8 place-items-center rounded-full border-2 border-[#0b3b24] bg-[#e2e9c4] text-[9px] font-bold text-[#536c19]">SM</span><span className="grid size-8 place-items-center rounded-full border-2 border-[#0b3b24] bg-[#cde5e0] text-[9px] font-bold text-[#17645c]">AE</span><span className="ml-2 self-center text-[10px] text-white/60">Shared ownership</span></div></div>
                <div className="rounded-xl border border-slate-200 bg-white p-4 sm:p-5"><div className="flex items-center justify-between"><div><p className="text-xs font-semibold text-slate-800">Pipeline stage</p><p className="mt-1 text-[10px] text-slate-400">A visible, controlled progression</p></div><span className="rounded-full bg-[#edf5f0] px-2 py-1 text-[9px] font-semibold uppercase tracking-wide text-[#287248]">In progress</span></div>
                  <div className="mt-6 flex items-center"><StagePill label="L1" complete/><span className="landing-progress-line"/><StagePill label="L2" complete/><span className="landing-progress-line landing-progress-active"/><StagePill label="L3" active/><span className="landing-progress-line landing-progress-muted"/><StagePill label="Won"/></div>
                  <div className="mt-5 grid grid-cols-2 gap-2"><MiniInfo icon={Building2} label="Organisation" value="Customer account"/><MiniInfo icon={UsersRound} label="Stakeholders" value="Contacts connected"/></div>
                  <div className="mt-3 flex items-center gap-2 rounded-lg bg-[#f5f8f5] px-3 py-2.5"><span className="grid size-7 place-items-center rounded-md bg-white text-[#287248] shadow-sm"><Check className="size-3.5"/></span><p className="text-[10px] leading-4 text-slate-600">Meetings, notes, and next steps stay with the opportunity.</p></div>
                </div>
              </div>
              <div className="mt-3 flex flex-wrap items-center justify-between gap-2 rounded-xl border border-slate-200/80 bg-white px-4 py-3"><div className="flex items-center gap-2"><span className="size-2 rounded-full bg-[#10b981]"/><span className="text-[10px] font-medium text-slate-600">A shared view from discovery to close</span></div><span className="text-[9px] font-semibold uppercase tracking-[.12em] text-slate-400">BDR <i className="mx-1 not-italic text-[#77a88b]">→</i> MANAGER <i className="mx-1 not-italic text-[#77a88b]">→</i> AE</span></div>
            </div>
          </div>
        </div>
      </section>

      <section id="journey" className="mx-auto max-w-7xl scroll-mt-8 px-5 py-20 sm:px-8 sm:py-24 lg:px-10">
        <div className="mx-auto max-w-2xl text-center"><p className="text-xs font-bold uppercase tracking-[.18em] text-[#398257]">How the CRM works</p><h2 className="mt-3 text-3xl font-semibold tracking-[-.04em] text-[#092f1b] sm:text-4xl">A connected journey, with a clear next step.</h2><p className="mt-4 text-sm leading-6 text-slate-600 sm:text-base">Each person focuses on their part of the process. The opportunity history keeps the whole team in sync.</p></div>
        <div className="relative mt-12 grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
          <div className="landing-journey-rail absolute left-[9%] right-[9%] top-6 hidden h-px lg:block" aria-hidden="true"/>
          {flow.map((step,index)=>{const Icon=step.icon;return <article key={step.number} className="landing-flow-card relative rounded-2xl border border-slate-200 bg-white p-5 shadow-[0_8px_30px_-26px_rgba(5,63,26,.45)] transition duration-300 hover:-translate-y-1 hover:border-[#b9d4c1] hover:shadow-[0_20px_44px_-30px_rgba(5,63,26,.38)]" style={{animationDelay:`${index*80}ms`}}><div className="relative z-[1] flex items-center justify-between"><span className="grid size-11 place-items-center rounded-xl border border-[#dcebe1] bg-[#f2f7f3] text-[#0b3b24] shadow-[0_0_0_6px_#f7f8f6]"><Icon className="size-5"/></span><span className="font-mono text-[10px] font-semibold text-[#76a88a]">{step.n}</span></div><p className="mt-6 text-[9px] font-bold uppercase tracking-[.13em] text-[#398257]">{step.role}</p><h3 className="mt-1 text-base font-semibold text-slate-900">{step.title}</h3><p className="mt-2 text-xs leading-5 text-slate-600">{step.text}</p></article>})}
        </div>
      </section>

      <section id="teams" className="scroll-mt-8 border-y border-[#e6ece7] bg-white py-20 sm:py-24">
        <div className="mx-auto max-w-7xl px-5 sm:px-8 lg:px-10"><div className="flex flex-col justify-between gap-5 md:flex-row md:items-end"><div className="max-w-2xl"><p className="text-xs font-bold uppercase tracking-[.18em] text-[#398257]">Made for the full sales team</p><h2 className="mt-3 text-3xl font-semibold tracking-[-.04em] text-[#092f1b] sm:text-4xl">Focused workspaces. Shared momentum.</h2></div><p className="max-w-md text-sm leading-6 text-slate-600">The right information reaches the next person without losing the decisions that got it there.</p></div>
          <div className="mt-10 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">{ownership.map((item,index)=>{const Icon=item.icon;return <article key={item.label} className="group relative overflow-hidden rounded-2xl border border-slate-200 bg-[#fcfdfc] p-5 transition duration-300 hover:-translate-y-1 hover:border-[#c3d8c9] hover:shadow-lg"><div className="flex items-center justify-between"><span className={`grid size-10 place-items-center rounded-xl ${item.tint}`}><Icon className="size-[18px]"/></span><span className="rounded-full border border-slate-200 bg-white px-2 py-1 text-[9px] font-bold tracking-[.12em] text-slate-400">{item.label}</span></div><p className="mt-5 text-[10px] font-semibold uppercase tracking-wide text-[#398257]">{item.role}</p><h3 className="mt-2 text-lg font-semibold leading-6 tracking-tight text-slate-900">{item.title}</h3><p className="mt-3 text-sm leading-6 text-slate-600">{item.text}</p><span className="pointer-events-none absolute -bottom-8 -right-7 size-24 rounded-full border-[16px] border-[#edf5f0] opacity-0 transition duration-300 group-hover:opacity-100" aria-hidden="true"/></article>})}</div>
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-5 py-20 sm:px-8 sm:py-24 lg:px-10"><div className="relative overflow-hidden rounded-[1.7rem] bg-[#082f1b] px-6 py-10 text-white sm:px-10 sm:py-12 lg:px-14"><div className="absolute -right-16 -top-36 size-80 rounded-full border border-white/10" aria-hidden="true"/><div className="absolute -right-4 -top-24 size-56 rounded-full border border-white/[.07]" aria-hidden="true"/><div className="relative flex flex-col justify-between gap-7 md:flex-row md:items-center"><div className="max-w-2xl"><p className="text-[10px] font-bold uppercase tracking-[.18em] text-[#9bdab0]">Your workspace is ready</p><h2 className="mt-3 text-2xl font-semibold tracking-[-.035em] sm:text-3xl">Bring the sales journey into focus.</h2><p className="mt-3 text-sm leading-6 text-white/65">Sign in with the account provided by your Prosperr administrator.</p></div><Link to="/login"><Button className="h-12 shrink-0 rounded-lg bg-[#75d99b] px-6 font-semibold text-[#082f1b] transition hover:-translate-y-0.5 hover:bg-[#91e5ae]">Sign in to Prosperr <ArrowRight className="ml-1 size-4"/></Button></Link></div></div></section>
    </main>
    <footer className="border-t border-[#e4ebe5] bg-white"><div className="mx-auto flex max-w-7xl flex-col gap-3 px-5 py-5 text-xs text-slate-500 sm:flex-row sm:items-center sm:justify-between sm:px-8 lg:px-10"><Brand/><span className="inline-flex items-center gap-1.5"><ShieldCheck className="size-3.5 text-[#398257]"/> A role-based sales workspace</span></div></footer>
  </div>;
}

function Brand({light=false}) { return <Link to="/" className="inline-flex items-center gap-2.5 rounded-md focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#75d99b]" aria-label="Prosperr Sales CRM home"><span className="grid size-9 shrink-0 place-items-center rounded-xl bg-white p-1.5"><img src="/prosperr-mark.png" alt="" className="size-6 object-contain"/></span><span><strong className={`block text-sm leading-5 tracking-tight ${light?"text-white":"text-[#053f1a]"}`}>Prosperr</strong><span className={`block text-[9px] font-semibold uppercase tracking-[.16em] ${light?"text-white/50":"text-slate-500"}`}>Sales CRM</span></span></Link>; }
function StagePill({label,complete=false,active=false}) { return <span className={`relative z-[1] grid size-7 shrink-0 place-items-center rounded-full text-[9px] font-bold ${complete?"bg-[#0b3b24] text-white":active?"border-2 border-[#0b3b24] bg-[#eaf3ed] text-[#0b3b24]":"border border-slate-200 bg-white text-slate-400"}`}>{complete?<Check className="size-3.5"/>:label}</span>; }
function MiniInfo({icon:Icon,label,value}) { return <div className="flex min-w-0 items-center gap-2 rounded-lg border border-slate-100 px-2.5 py-2"><span className="grid size-7 shrink-0 place-items-center rounded-md bg-[#f3f7f4] text-[#398257]"><Icon className="size-3.5"/></span><span className="min-w-0"><small className="block truncate text-[9px] text-slate-400">{label}</small><b className="block truncate text-[10px] font-semibold text-slate-700">{value}</b></span></div>; }
