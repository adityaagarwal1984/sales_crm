# Business Workflows

**Status: Partial implementation.** The BDR-owned lead, calling, follow-up, and SQL submission path is implemented. SQL qualification and manager review remain unconfirmed.

## 1. BDR discovery and submission

1. BDR discovers an organisation and adds its contacts.
2. BDR creates a sales pursuit (lead) linked to an organisation and primary contact.
3. Each call appends an immutable activity record; an optional next follow-up creates a separate pending record.
4. Completing a follow-up appends another call and links it to the completed follow-up; optional next follow-up creates a new pending record.
5. BDR submits a lead as an SQL, creating a separate record in `SUBMITTED` state.
6. SALES_MANAGER receives the SQL submission with its lead, organisation, and primary contact context.
7. SALES_MANAGER reviews and assigns it to an AE in the next workflow block.

**Unknown:** qualification criteria, required evidence/fields, duplicate handling, rejection/rework path, review SLA, assignment rules, and whether assignment requires AE acceptance. The SQL intake queue is implemented; review decisions and AE assignment are not. Leads and their activities are visible to their owning BDR; managers see linked lead context through submissions.

## 2. Manager review and assignment

1. SALES_MANAGER views pending submissions.
2. Manager reviews and records a decision.
3. An accepted submission is assigned to an AE.

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
