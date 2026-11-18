# Business Workflows

**Status: Implemented through SQL qualification review.** AE assignment and later sales stages remain for subsequent blocks.

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
11. An approved qualification is marked eligible for AE assignment. The manager-to-AE assignment occurs in a later workflow block.

**Still to confirm:** detailed acceptance criteria, deal-value currency, review SLA, assignment rules, and whether assignment requires AE acceptance. Leads and their activities remain visible to their owning BDR; Sales Managers receive submitted qualifications and linked lead/contact context.

## 2. Manager review and assignment

1. SALES_MANAGER views submitted SQL qualifications and their history.
2. Manager approves or rejects with a reason; the decision actor and time are recorded.
3. A later workflow assigns approved qualifications to AEs.

**Unknown:** allowed decisions, approval authority, assignment load-balancing, reassignment, notification, and audit requirements.

## 3. AE opportunity progression

1. AE works assigned opportunities and maintains stakeholders.
2. AE records L1/L2/L3 meetings.
3. AE records Won or Lost and, when Won, creates an implementation handover.

**Unknown:** stage order and skip/repeat rules, evidence at each stage, forecast amount, close reasons, who can reopen, and handover acceptance.

## 4. Inactive customer revival

1. SALES_MANAGER identifies or receives an inactive customer.
2. Revival work is assigned and followed up.

**Unknown:** inactivity threshold, revival owner, outcome states, link to new/old opportunity, and reporting rules.

## 5. Targets and leadership reporting

1. ADMIN sets targets (scope and metric are undefined).
2. Relevant managers and LEADERSHIP review team/organization performance and funnel analytics.

**Unknown:** target periods, metric definitions, data visibility, historical snapshots, and treatment of reassignment/credit splits.
