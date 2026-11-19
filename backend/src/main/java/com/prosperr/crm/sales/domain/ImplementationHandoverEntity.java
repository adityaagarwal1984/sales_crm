package com.prosperr.crm.sales.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "implementation_handover")
public class ImplementationHandoverEntity {
    @Id @Column(nullable = false, updatable = false) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "opportunity_id", nullable = false, updatable = false) private OpportunityEntity opportunity;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "generated_by", nullable = false, updatable = false) private UserEntity generatedBy;
    @Column(nullable = false, length = 24) private String status = "PENDING";
    @Column(name = "final_deal_value", nullable = false, precision = 14, scale = 2) private BigDecimal finalDealValue;
    @Column(name = "currency_code", nullable = false, length = 3) private String currencyCode;
    @Column(name = "commercial_details", nullable = false, length = 10000) private String commercialDetails;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    protected ImplementationHandoverEntity() {}
    public ImplementationHandoverEntity(OpportunityEntity opportunity, UserEntity generatedBy, BigDecimal finalDealValue, String currencyCode, String commercialDetails) { this.opportunity = opportunity; this.generatedBy = generatedBy; this.finalDealValue = finalDealValue; this.currencyCode = currencyCode; this.commercialDetails = commercialDetails; }
    @PrePersist void insert() { if (id == null) id = UUID.randomUUID(); if (createdAt == null) createdAt = Instant.now(); }
    public UUID getId() { return id; } public OpportunityEntity getOpportunity() { return opportunity; } public UserEntity getGeneratedBy() { return generatedBy; }
    public String getStatus() { return status; } public Instant getCreatedAt() { return createdAt; }
    public BigDecimal getFinalDealValue() { return finalDealValue; } public String getCurrencyCode() { return currencyCode; } public String getCommercialDetails() { return commercialDetails; }
}
