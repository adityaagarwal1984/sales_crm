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
- SQL submission is duplicated, incomplete, rejected, resubmitted, or changed after review.
- Submission is assigned while the AE is inactive or at capacity.
- Meeting stages are skipped/repeated, or a meeting is cancelled/no-show.
- Opportunity is marked Won/Lost more than once, reopened, or handed over twice.
- Revival work is duplicated or the customer becomes active during revival.

## Reporting and persistence

- Empty dataset, large result set, timezone boundary, daylight-saving boundary, and date-range inclusivity.
- Null/zero amount, currency precision, currency changes, and exchange-rate reporting.
- Data updates during an analytics query; clarify whether dashboards are live or period snapshots.
- Database migration fails partway, application restarts, or the database is unavailable.

## Security and reliability

- Invalid/expired/replayed token; brute-force login; password reset and credential leakage.
- Invalid sort/filter parameters, oversized request, malformed input, and unexpected database constraint violation.
- Duplicate form submission from retry or double-click; define idempotency for high-impact writes.
- Logs, error responses, exports, or API docs accidentally reveal sensitive information.
