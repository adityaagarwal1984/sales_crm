package com.prosperr.crm.organisation.domain;

import jakarta.persistence.*;
import com.prosperr.crm.identity.domain.UserEntity;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contact")
public class ContactEntity {
    @Id @Column(nullable = false, updatable = false) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organisation_id", nullable = false) private OrganisationEntity organisation;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "created_by", nullable = false, updatable = false) private UserEntity createdBy;
    @Column(nullable = false, length = 180) private String name;
    @Column(length = 320) private String email;
    @Column(length = 40) private String phone;
    @Column(length = 120) private String designation;
    @Column(length = 120) private String department;
    @Column(name = "linkedin_url", length = 500) private String linkedinUrl;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Version @Column(nullable = false) private long version;

    protected ContactEntity() {}
    public ContactEntity(OrganisationEntity organisation,UserEntity createdBy,String name,String email,String phone,String designation,String department,String linkedinUrl){this.organisation=organisation;this.createdBy=createdBy;this.name=name;this.email=email;this.phone=phone;this.designation=designation;this.department=department;this.linkedinUrl=linkedinUrl;}
    @PrePersist void insert(){Instant now=Instant.now();if(id==null)id=UUID.randomUUID();if(createdAt==null)createdAt=now;updatedAt=now;}
    @PreUpdate void updateTimestamp(){updatedAt=Instant.now();}
    public void update(OrganisationEntity organisation,String name,String email,String phone,String designation,String department,String linkedinUrl){this.organisation=organisation;this.name=name;this.email=email;this.phone=phone;this.designation=designation;this.department=department;this.linkedinUrl=linkedinUrl;}
    public UUID getId(){return id;} public OrganisationEntity getOrganisation(){return organisation;} public UserEntity getCreatedBy(){return createdBy;} public String getName(){return name;} public String getEmail(){return email;} public String getPhone(){return phone;} public String getDesignation(){return designation;} public String getDepartment(){return department;} public String getLinkedinUrl(){return linkedinUrl;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
