package com.prosperr.crm.sales.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import com.prosperr.crm.prospecting.domain.SqlQualificationEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sales_opportunity")
public class OpportunityEntity {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sql_qualification_id", nullable = false, unique = true, updatable = false)
    private SqlQualificationEntity qualification;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_ae_id", nullable = false)
    private UserEntity assignedAe;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_by", nullable = false, updatable = false)
    private UserEntity assignedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OpportunityStatus status = OpportunityStatus.L1;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(name = "final_deal_value", precision = 14, scale = 2)
    private BigDecimal finalDealValue;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "commercial_details", length = 10000)
    private String commercialDetails;

    @Column(name = "lost_reason", length = 500)
    private String lostReason;

    @Column(name = "closure_notes", length = 10000)
    private String closureNotes;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private long version;

    protected OpportunityEntity() {}

    public OpportunityEntity(SqlQualificationEntity qualification, UserEntity assignedAe,
            UserEntity assignedBy, BigDecimal amount, String currencyCode, Instant assignedAt) {
        this.qualification = qualification;
        this.assignedAe = assignedAe;
        this.assignedBy = assignedBy;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.assignedAt = assignedAt;
    }

    @PrePersist
    void beforeInsert() {
        Instant now = Instant.now();
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    void beforeUpdate() { updatedAt = Instant.now(); }

    public void reassign(UserEntity ae, Instant at) {
        this.assignedAe = ae;
        this.assignedAt = at;
    }

    public void advanceStage(OpportunityStatus next) { this.status = next; }
    public void closeWon(BigDecimal finalDealValue, Instant closedAt, String commercialDetails) {
        this.status = OpportunityStatus.WON;
        this.finalDealValue = finalDealValue;
        this.closedAt = closedAt;
        this.commercialDetails = commercialDetails;
        this.lostReason = null;
    }
    public void closeLost(Instant closedAt, String lostReason, String closureNotes) {
        this.status = OpportunityStatus.LOST;
        this.closedAt = closedAt;
        this.lostReason = lostReason;
        this.closureNotes = closureNotes;
        this.finalDealValue = null;
    }
    public void reopen() {
        this.status = OpportunityStatus.L3;
        this.finalDealValue = null;
        this.closedAt = null;
        this.commercialDetails = null;
        this.lostReason = null;
        this.closureNotes = null;
    }

    public UUID getId() { return id; }
    public SqlQualificationEntity getQualification() { return qualification; }
    public UserEntity getAssignedAe() { return assignedAe; }
    public UserEntity getAssignedBy() { return assignedBy; }
    public OpportunityStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrencyCode() { return currencyCode; }
    public BigDecimal getFinalDealValue() { return finalDealValue; }
    public Instant getClosedAt() { return closedAt; }
    public String getCommercialDetails() { return commercialDetails; }
    public String getLostReason() { return lostReason; }
    public String getClosureNotes() { return closureNotes; }
    public Instant getAssignedAt() { return assignedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
