package com.prosperr.crm.sales.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "opportunity_assignment_history")
public class OpportunityAssignmentHistoryEntity {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "opportunity_id", nullable = false, updatable = false)
    private OpportunityEntity opportunity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_ae_id", updatable = false)
    private UserEntity previousAe;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_ae_id", nullable = false, updatable = false)
    private UserEntity assignedAe;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assigned_by", nullable = false, updatable = false)
    private UserEntity assignedBy;

    @Column(nullable = false, length = 20, updatable = false)
    private String action;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected OpportunityAssignmentHistoryEntity() {}

    public OpportunityAssignmentHistoryEntity(OpportunityEntity opportunity, UserEntity previousAe,
            UserEntity assignedAe, UserEntity assignedBy, String action) {
        this.opportunity = opportunity;
        this.previousAe = previousAe;
        this.assignedAe = assignedAe;
        this.assignedBy = assignedBy;
        this.action = action;
    }

    @PrePersist
    void beforeInsert() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public OpportunityEntity getOpportunity() { return opportunity; }
    public UserEntity getPreviousAe() { return previousAe; }
    public UserEntity getAssignedAe() { return assignedAe; }
    public UserEntity getAssignedBy() { return assignedBy; }
    public String getAction() { return action; }
    public Instant getCreatedAt() { return createdAt; }
}
