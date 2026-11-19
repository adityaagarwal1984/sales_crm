package com.prosperr.crm.sales.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import com.prosperr.crm.organisation.domain.ContactEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "opportunity_stakeholder", uniqueConstraints = @UniqueConstraint(name = "uq_opportunity_stakeholder_contact", columnNames = {"opportunity_id", "contact_id"}))
public class OpportunityStakeholderEntity {
    @Id @Column(nullable = false, updatable = false) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "opportunity_id", nullable = false, updatable = false) private OpportunityEntity opportunity;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "contact_id", nullable = false, updatable = false) private ContactEntity contact;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "added_by", nullable = false, updatable = false) private UserEntity addedBy;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 32) private OpportunityStakeholderRole role;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    protected OpportunityStakeholderEntity() {}
    public OpportunityStakeholderEntity(OpportunityEntity opportunity, ContactEntity contact, UserEntity addedBy, OpportunityStakeholderRole role) { this.opportunity = opportunity; this.contact = contact; this.addedBy = addedBy; this.role = role; }
    @PrePersist void insert() { if (id == null) id = UUID.randomUUID(); if (createdAt == null) createdAt = Instant.now(); }
    public UUID getId() { return id; } public OpportunityEntity getOpportunity() { return opportunity; } public ContactEntity getContact() { return contact; }
    public UserEntity getAddedBy() { return addedBy; } public OpportunityStakeholderRole getRole() { return role; } public Instant getCreatedAt() { return createdAt; }
}
