package com.prosperr.crm.prospecting.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import com.prosperr.crm.organisation.domain.ContactEntity;
import com.prosperr.crm.organisation.domain.OrganisationEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="sales_lead")
public class LeadEntity {
    @Id @Column(nullable=false,updatable=false) private UUID id;
    @Column(nullable=false,length=180) private String title;
    @Column(length=10000) private String description;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="organisation_id",nullable=false) private OrganisationEntity organisation;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="primary_contact_id",nullable=false) private ContactEntity primaryContact;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="owner_id",nullable=false) private UserEntity owner;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="created_by",nullable=false,updatable=false) private UserEntity createdBy;
    @Enumerated(EnumType.STRING) @Column(name="last_call_outcome",length=32) private CallOutcome lastCallOutcome;
    @Column(name="last_call_at") private Instant lastCallAt;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    @Column(name="updated_at",nullable=false) private Instant updatedAt;
    @Version @Column(nullable=false) private long version;

    protected LeadEntity() {}
    public LeadEntity(String title,String description,OrganisationEntity organisation,ContactEntity primaryContact,UserEntity owner,UserEntity createdBy){this.title=title;this.description=description;this.organisation=organisation;this.primaryContact=primaryContact;this.owner=owner;this.createdBy=createdBy;}
    @PrePersist void insert(){Instant now=Instant.now();if(id==null)id=UUID.randomUUID();if(createdAt==null)createdAt=now;updatedAt=now;}
    @PreUpdate void updateTimestamp(){updatedAt=Instant.now();}
    public void update(String title,String description,OrganisationEntity organisation,ContactEntity primaryContact){this.title=title;this.description=description;this.organisation=organisation;this.primaryContact=primaryContact;}
    public void recordLatestCall(CallOutcome outcome,Instant calledAt){if(lastCallAt==null||!calledAt.isBefore(lastCallAt)){lastCallAt=calledAt;lastCallOutcome=outcome;}}
    public UUID getId(){return id;} public String getTitle(){return title;} public String getDescription(){return description;} public OrganisationEntity getOrganisation(){return organisation;} public ContactEntity getPrimaryContact(){return primaryContact;} public UserEntity getOwner(){return owner;} public UserEntity getCreatedBy(){return createdBy;} public CallOutcome getLastCallOutcome(){return lastCallOutcome;} public Instant getLastCallAt(){return lastCallAt;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
