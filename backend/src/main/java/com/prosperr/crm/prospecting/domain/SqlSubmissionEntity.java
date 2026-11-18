package com.prosperr.crm.prospecting.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name="sql_submission")
public class SqlSubmissionEntity {
    @Id @Column(nullable=false,updatable=false) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="lead_id",nullable=false,updatable=false) private LeadEntity lead;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="submitted_by",nullable=false,updatable=false) private UserEntity submittedBy;
    @Column(name="submitted_at",nullable=false,updatable=false) private Instant submittedAt;
    @Column(nullable=false,length=20,updatable=false) private String status="SUBMITTED";

    protected SqlSubmissionEntity() {}
    public SqlSubmissionEntity(LeadEntity lead,UserEntity submittedBy){this.lead=lead;this.submittedBy=submittedBy;}
    @PrePersist void insert(){if(id==null)id=UUID.randomUUID();if(submittedAt==null)submittedAt=Instant.now();}
    public UUID getId(){return id;} public LeadEntity getLead(){return lead;} public UserEntity getSubmittedBy(){return submittedBy;} public Instant getSubmittedAt(){return submittedAt;} public String getStatus(){return status;}
}
