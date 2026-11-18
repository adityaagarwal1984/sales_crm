package com.prosperr.crm.prospecting.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import com.prosperr.crm.organisation.domain.ContactEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name="call_activity")
public class CallActivityEntity {
    @Id @Column(nullable=false,updatable=false) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="lead_id",nullable=false,updatable=false) private LeadEntity lead;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="contact_id",nullable=false,updatable=false) private ContactEntity contact;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="created_by",nullable=false,updatable=false) private UserEntity createdBy;
    @Column(name="called_at",nullable=false,updatable=false) private Instant calledAt;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=32,updatable=false) private CallOutcome outcome;
    @Column(length=10000,updatable=false) private String notes;
    @Column(name="next_follow_up_at",updatable=false) private Instant nextFollowUpAt;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;

    protected CallActivityEntity() {}
    public CallActivityEntity(LeadEntity lead,ContactEntity contact,UserEntity createdBy,Instant calledAt,CallOutcome outcome,String notes,Instant nextFollowUpAt){this.lead=lead;this.contact=contact;this.createdBy=createdBy;this.calledAt=calledAt;this.outcome=outcome;this.notes=notes;this.nextFollowUpAt=nextFollowUpAt;}
    @PrePersist void insert(){if(id==null)id=UUID.randomUUID();if(createdAt==null)createdAt=Instant.now();}
    public UUID getId(){return id;} public LeadEntity getLead(){return lead;} public ContactEntity getContact(){return contact;} public UserEntity getCreatedBy(){return createdBy;} public Instant getCalledAt(){return calledAt;} public CallOutcome getOutcome(){return outcome;} public String getNotes(){return notes;} public Instant getNextFollowUpAt(){return nextFollowUpAt;} public Instant getCreatedAt(){return createdAt;}
}
