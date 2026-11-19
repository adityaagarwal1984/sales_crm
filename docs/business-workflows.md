# Business Workflows

**Status: Implemented through AE lifecycle and Leadership analytics.** Revival (Block 7) and target management remain pending.

## 1. BDR discovery and submission

1. BDR discovers an organisation and adds its contacts.
2. BDR creates a sales pursuit (lead) linked to an organisation and primary contact.
3. Each call appends an immutable activity record; an optional next follow-up creates a separate pending record.
4. Completing a follow-up appends another call and links it to the completed follow-up; optional next follow-up creates a new pending record.
5. After an interested call, the BDR creates a qualification draft linked to that lead, organisation, and primary contact.
6. BDR records company size, industry, requirement, decision-maker information, estimated value, timeline, notes, and supporting information.
7. BDR may save an incomplete draft. Submission requires a business requirement, decision-maker yes/no, deal value, and timeline; details are required when a decision-maker is identified.
8. Submission enters the Sales Manager review queue. Drafts remain visible only to their owning BDR.
9. Sales Manager reviews and approves or rejects with a reason. Reviewer and timestamp are saved, and every create/save/submit/decision appends a history snapshot.
10. A rejected qualification can be revised and resubmitted. Only one qualification record exists for a lead, retaining its full decision history.
11. An approved qualification is eligible for AE assignment.

**Still to confirm:** detailed acceptance criteria, deal-value currency, review SLA, assignment rules, and whether assignment requires AE acceptance. Leads and their activities remain visible to their owning BDR; Sales Managers receive submitted qualifications and linked lead/contact context.

## 2. Manager review and assignment

1. Admin creates BDR and AE accounts. Every Sales Manager can see all BDR submissions and all AE accounts; the optional manager link does not restrict access.
2. SALES_MANAGER views submitted SQL qualifications and their history.
3. Manager approves or rejects with a reason; the decision actor and time are recorded.
4. Any Sales Manager assigns an approved SQL to any active AE, creating one opportunity and an assignment-history event.
5. Manager can reassign an active opportunity to another active AE. Closed opportunities cannot be reassigned. Every assignment/reassignment notifies the new AE in the app.
6. Manager views current opportunity ownership, assignment history, and BDR/AE performance.

**Leadership metric rules:** revenue is final Won deal value; SQL conversion is approved SQLs ÷ submitted SQLs; assignment time is average time from approval to first assignment. Amounts display in INR pending confirmation of the official reporting currency. Expired customer accounts remain unconfigured pending customer lifecycle and expiry-date definitions.

## 3. AE opportunity progression

1. AE views opportunities currently assigned to their account with organisation, associated contacts, qualification, and assignment history.
2. AE progresses L1 → L2 → L3. Skipping a stage is permitted only with a reason; closing is only available from L3.
3. Meeting type and opportunity stage are separate. A completed L1 or L2 meeting advances the matching stage; L3 meetings do not close the deal.
4. AE manages multiple stakeholders and follow-ups; every meeting, stage transition, assignment, and closure is retained in history.
5. WON requires final deal value and commercial details and creates an implementation handover. LOST requires a reason and closure notes.
6. An AE requests reopening; a Sales Manager approves or rejects with a reason. Approved reopening resumes at L3. A later WON closure creates another closure and handover record.

## 4. Inactive customer revival

1. SALES_MANAGER identifies or receives an inactive customer.
2. Revival work is assigned and followed up.

**Unknown:** inactivity threshold, revival owner, outcome states, link to new/old opportunity, and reporting rules.

## 5. Targets and leadership reporting

1. ADMIN sets targets (scope and metric are undefined).
2. Relevant managers and LEADERSHIP review team/organization performance and funnel analytics.

**Unknown:** target periods, metric definitions, data visibility, historical snapshots, and treatment of reassignment/credit splits.
