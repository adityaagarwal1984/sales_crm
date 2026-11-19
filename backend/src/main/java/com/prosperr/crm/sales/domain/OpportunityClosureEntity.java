package com.prosperr.crm.sales.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "opportunity_closure")
public class OpportunityClosureEntity {
    @Id @Column(nullable = false, updatable = false) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "opportunity_id", nullable = false, updatable = false) private OpportunityEntity opportunity;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "closed_by", nullable = false, updatable = false) private UserEntity closedBy;
    @Column(name = "outcome", nullable = false, length = 8, updatable = false) private String outcome;
    @Column(name = "final_deal_value", precision = 14, scale = 2, updatable = false) private BigDecimal finalDealValue;
    @Column(name = "currency_code", nullable = false, length = 3, updatable = false) private String currencyCode;
    @Column(name = "closed_at", nullable = false, updatable = false) private Instant closedAt;
    @Column(name = "commercial_details", length = 10000, updatable = false) private String commercialDetails;
    @Column(name = "lost_reason", length = 500, updatable = false) private String lostReason;
    @Column(name = "closure_notes", length = 10000, updatable = false) private String closureNotes;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    protected OpportunityClosureEntity() {}
    public OpportunityClosureEntity(OpportunityEntity opportunity, UserEntity closedBy, String outcome, BigDecimal finalDealValue, String currencyCode, Instant closedAt, String commercialDetails, String lostReason, String closureNotes) {
        this.opportunity = opportunity; this.closedBy = closedBy; this.outcome = outcome; this.finalDealValue = finalDealValue; this.currencyCode = currencyCode; this.closedAt = closedAt; this.commercialDetails = commercialDetails; this.lostReason = lostReason; this.closureNotes = closureNotes;
    }
    @PrePersist void insert() { if (id == null) id = UUID.randomUUID(); if (createdAt == null) createdAt = Instant.now(); }
    public UUID getId() { return id; } public OpportunityEntity getOpportunity() { return opportunity; } public UserEntity getClosedBy() { return closedBy; }
    public String getOutcome() { return outcome; } public BigDecimal getFinalDealValue() { return finalDealValue; } public String getCurrencyCode() { return currencyCode; }
    public Instant getClosedAt() { return closedAt; } public String getCommercialDetails() { return commercialDetails; } public String getLostReason() { return lostReason; } public String getClosureNotes() { return closureNotes; } public Instant getCreatedAt() { return createdAt; }
}
