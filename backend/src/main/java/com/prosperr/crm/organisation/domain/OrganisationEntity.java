package com.prosperr.crm.organisation.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organisation")
public class OrganisationEntity {
    @Id @Column(nullable = false, updatable = false) private UUID id;
    @Column(nullable = false, length = 180) private String name;
    @Column(length = 500) private String website;
    @Column(length = 120) private String industry;
    @Column(name = "company_size", length = 80) private String companySize;
    @Column(length = 180) private String location;
    @Column(length = 10000) private String address;
    @Column(name = "lead_source", length = 120) private String leadSource;
    @Column(length = 10000) private String description;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "created_by", nullable = false, updatable = false) private UserEntity createdBy;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Version @Column(nullable = false) private long version;

    protected OrganisationEntity() {}
    public OrganisationEntity(String name, String website, String industry, String companySize, String location, String address, String leadSource, String description, UserEntity createdBy) {
        this.name=name; this.website=website; this.industry=industry; this.companySize=companySize; this.location=location; this.address=address; this.leadSource=leadSource; this.description=description; this.createdBy=createdBy;
    }
    @PrePersist void insert() { Instant now=Instant.now(); if(id==null)id=UUID.randomUUID(); if(createdAt==null)createdAt=now; updatedAt=now; }
    @PreUpdate void updateTimestamp() { updatedAt=Instant.now(); }
    public void update(String name,String website,String industry,String companySize,String location,String address,String leadSource,String description) { this.name=name;this.website=website;this.industry=industry;this.companySize=companySize;this.location=location;this.address=address;this.leadSource=leadSource;this.description=description; }
    public UUID getId(){return id;} public String getName(){return name;} public String getWebsite(){return website;} public String getIndustry(){return industry;} public String getCompanySize(){return companySize;} public String getLocation(){return location;} public String getAddress(){return address;} public String getLeadSource(){return leadSource;} public String getDescription(){return description;} public UserEntity getCreatedBy(){return createdBy;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
