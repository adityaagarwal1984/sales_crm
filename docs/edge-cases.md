# Edge Cases

**Status: Initial risk list; expand into acceptance criteria before each feature.**

## Identity and access

- Inactive user attempts sign-in or an existing token is used after deactivation.
- User has multiple roles, changes team, or loses assignment while viewing a record.
- User guesses another record's identifier or directly calls an API outside their role/ownership scope.
- No administrator exists; last administrator is deactivated; role assignment creates privilege escalation.

## Organisations and contacts

- Duplicate organisation/contact is entered with alternate spelling, domain, or email.
- Contact changes employer, lacks an email, or shares an email address.
- Concurrent users edit the same record; one user's update could overwrite the other's changes.
- Organisation is archived while opportunities or follow-ups remain open.

## Follow-ups and workflow

- Follow-up is overdue, rescheduled, completed late, or assigned to a deactivated user.
- SQL qualification is duplicated, incomplete, rejected, resubmitted, or changed after approval; drafts must remain private to the BDR and every transition must appear in immutable history.
- SQL is assigned before approval, to an inactive or non-AE account; assignment is repeated to the same AE; an opportunity is reassigned after closing; assignment history or AE notification fails.
- A manager requests another team's SQL/opportunity by ID; an unassigned legacy BDR/AE appears in team metrics; an AE reads or updates another AE's assignment.
- Customer expiry, conversion denominator, or revenue currency is missing or ambiguous; dashboards must label unconfigured values instead of manufacturing counts.
- Submission is assigned while the AE is inactive or at capacity.
- Stage skipping without a reason, an attempt to move backwards, or an attempt to close before L3.
- An L1/L2 meeting is logged more than once; meeting level does not match current stage; follow-up is completed by another AE.
- Stakeholder is outside the opportunity's organisation or same contact/role is added twice.
- Opportunity is marked Won/Lost more than once, reopened without manager approval, or closed after reopening; each close must keep its own immutable record and each Won close its own handover.
- Revival work is duplicated or the customer becomes active during revival.

## Reporting and persistence

- Empty dataset, large result set, timezone boundary, daylight-saving boundary, and date-range inclusivity.
- Null/zero amount, currency precision, currency changes, and exchange-rate reporting.
- Data updates during an analytics query; clarify whether dashboards are live or period snapshots.
- Multiple contacts on one organisation must not inflate organisation count; SQL conversion uses approved ÷ submitted; assignment time uses approval to first assignment; revenue sums final Won deal value.
- Database migration fails partway, application restarts, or the database is unavailable.

## Security and reliability

- Invalid/expired/replayed token; brute-force login; password reset and credential leakage.
- Invalid sort/filter parameters, oversized request, malformed input, and unexpected database constraint violation.
- Duplicate form submission from retry or double-click; define idempotency for high-impact writes.
- Logs, error responses, exports, or API docs accidentally reveal sensitive information.
