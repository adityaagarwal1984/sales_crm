package com.prosperr.crm.sales.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "opportunity_stage_history")
public class OpportunityStageHistoryEntity {
    @Id @Column(nullable = false, updatable = false) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "opportunity_id", nullable = false, updatable = false) private OpportunityEntity opportunity;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "actor_id", nullable = false, updatable = false) private UserEntity actor;
    @Column(name = "from_stage", length = 20, updatable = false) private String fromStage;
    @Column(name = "to_stage", nullable = false, length = 20, updatable = false) private String toStage;
    @Column(length = 2000, updatable = false) private String reason;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;

    protected OpportunityStageHistoryEntity() {}
    public OpportunityStageHistoryEntity(OpportunityEntity opportunity, UserEntity actor, String fromStage, String toStage, String reason) {
        this.opportunity = opportunity; this.actor = actor; this.fromStage = fromStage; this.toStage = toStage; this.reason = reason;
    }
    @PrePersist void insert() { if (id == null) id = UUID.randomUUID(); if (createdAt == null) createdAt = Instant.now(); }
    public UUID getId() { return id; } public OpportunityEntity getOpportunity() { return opportunity; }
    public UserEntity getActor() { return actor; } public String getFromStage() { return fromStage; }
    public String getToStage() { return toStage; } public String getReason() { return reason; } public Instant getCreatedAt() { return createdAt; }
}
