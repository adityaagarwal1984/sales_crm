package com.prosperr.crm.sales.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "opportunity_reopen_request")
public class OpportunityReopenRequestEntity {
    @Id @Column(nullable = false, updatable = false) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "opportunity_id", nullable = false, updatable = false) private OpportunityEntity opportunity;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "requested_by", nullable = false, updatable = false) private UserEntity requestedBy;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "reviewed_by") private UserEntity reviewedBy;
    @Column(nullable = false, length = 2000, updatable = false) private String reason;
    @Column(nullable = false, length = 16) private String status = "PENDING";
    @Column(name = "review_reason", length = 2000) private String reviewReason;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "reviewed_at") private Instant reviewedAt;
    protected OpportunityReopenRequestEntity() {}
    public OpportunityReopenRequestEntity(OpportunityEntity opportunity, UserEntity requestedBy, String reason) { this.opportunity = opportunity; this.requestedBy = requestedBy; this.reason = reason; }
    @PrePersist void insert() { if (id == null) id = UUID.randomUUID(); if (createdAt == null) createdAt = Instant.now(); }
    public void review(UserEntity reviewer, boolean approve, String reason, Instant at) { this.reviewedBy = reviewer; this.status = approve ? "APPROVED" : "REJECTED"; this.reviewReason = reason; this.reviewedAt = at; }
    public UUID getId() { return id; } public OpportunityEntity getOpportunity() { return opportunity; } public UserEntity getRequestedBy() { return requestedBy; }
    public UserEntity getReviewedBy() { return reviewedBy; } public String getReason() { return reason; } public String getStatus() { return status; }
    public String getReviewReason() { return reviewReason; } public Instant getCreatedAt() { return createdAt; } public Instant getReviewedAt() { return reviewedAt; }
}
