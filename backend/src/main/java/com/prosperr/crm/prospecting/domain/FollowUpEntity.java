package com.prosperr.crm.prospecting.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import com.prosperr.crm.organisation.domain.ContactEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="follow_up")
public class FollowUpEntity {
    @Id @Column(nullable=false,updatable=false) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="lead_id",nullable=false,updatable=false) private LeadEntity lead;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="contact_id",nullable=false,updatable=false) private ContactEntity contact;
    @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="source_call_id",nullable=false,updatable=false,unique=true) private CallActivityEntity sourceCall;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="owner_id",nullable=false,updatable=false) private UserEntity owner;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="created_by",nullable=false,updatable=false) private UserEntity createdBy;
    @Column(name="due_at",nullable=false) private Instant dueAt;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private FollowUpStatus status=FollowUpStatus.PENDING;
    @Column(name="completed_at") private Instant completedAt;
    @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="completed_call_id",unique=true) private CallActivityEntity completedCall;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    @Column(name="updated_at",nullable=false) private Instant updatedAt;
    @Version @Column(nullable=false) private long version;

    protected FollowUpEntity() {}
    public FollowUpEntity(LeadEntity lead,ContactEntity contact,CallActivityEntity sourceCall,UserEntity owner,UserEntity createdBy,Instant dueAt){this.lead=lead;this.contact=contact;this.sourceCall=sourceCall;this.owner=owner;this.createdBy=createdBy;this.dueAt=dueAt;}
    @PrePersist void insert(){Instant now=Instant.now();if(id==null)id=UUID.randomUUID();if(createdAt==null)createdAt=now;updatedAt=now;}
    @PreUpdate void updateTimestamp(){updatedAt=Instant.now();}
    public void reschedule(Instant dueAt){if(status!=FollowUpStatus.PENDING)throw new IllegalStateException("Only pending follow-ups can be rescheduled");this.dueAt=dueAt;}
    public void complete(CallActivityEntity call,Instant completedAt){if(status!=FollowUpStatus.PENDING)throw new IllegalStateException("Only pending follow-ups can be completed");this.status=FollowUpStatus.COMPLETED;this.completedCall=call;this.completedAt=completedAt;}
    public UUID getId(){return id;} public LeadEntity getLead(){return lead;} public ContactEntity getContact(){return contact;} public CallActivityEntity getSourceCall(){return sourceCall;} public UserEntity getOwner(){return owner;} public UserEntity getCreatedBy(){return createdBy;} public Instant getDueAt(){return dueAt;} public FollowUpStatus getStatus(){return status;} public Instant getCompletedAt(){return completedAt;} public CallActivityEntity getCompletedCall(){return completedCall;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
