package com.prosperr.crm.prospecting.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="sql_qualification")
public class SqlQualificationEntity {
    @Id @Column(nullable=false,updatable=false) private UUID id;
    @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="lead_id",nullable=false,unique=true,updatable=false) private LeadEntity lead;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="created_by",nullable=false,updatable=false) private UserEntity createdBy;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="reviewed_by") private UserEntity reviewedBy;
    @Column(name="company_size",length=80) private String companySize;
    @Column(length=120) private String industry;
    @Column(name="business_requirement",length=10000) private String businessRequirement;
    @Column(name="decision_maker_identified") private Boolean decisionMakerIdentified;
    @Column(name="decision_maker_details",length=5000) private String decisionMakerDetails;
    @Column(name="estimated_deal_value",precision=14,scale=2) private BigDecimal estimatedDealValue;
    @Column(name="expected_timeline",length=120) private String expectedTimeline;
    @Column(name="qualification_notes",length=10000) private String qualificationNotes;
    @Column(name="supporting_information",length=10000) private String supportingInformation;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private SqlQualificationStatus status=SqlQualificationStatus.DRAFT;
    @Column(name="submitted_at") private Instant submittedAt;
    @Column(name="reviewed_at") private Instant reviewedAt;
    @Column(name="rejection_reason",length=5000) private String rejectionReason;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    @Column(name="updated_at",nullable=false) private Instant updatedAt;
    @Version @Column(nullable=false) private long version;

    protected SqlQualificationEntity() {}
    public SqlQualificationEntity(LeadEntity lead,UserEntity createdBy){this.lead=lead;this.createdBy=createdBy;this.companySize=lead.getOrganisation().getCompanySize();this.industry=lead.getOrganisation().getIndustry();}
    @PrePersist void insert(){Instant now=Instant.now();if(id==null)id=UUID.randomUUID();if(createdAt==null)createdAt=now;updatedAt=now;}
    @PreUpdate void updateTimestamp(){updatedAt=Instant.now();}
    public void saveDraft(String companySize,String industry,String businessRequirement,Boolean decisionMakerIdentified,String decisionMakerDetails,BigDecimal estimatedDealValue,String expectedTimeline,String qualificationNotes,String supportingInformation){
        this.companySize=companySize;this.industry=industry;this.businessRequirement=businessRequirement;this.decisionMakerIdentified=decisionMakerIdentified;this.decisionMakerDetails=decisionMakerDetails;this.estimatedDealValue=estimatedDealValue;this.expectedTimeline=expectedTimeline;this.qualificationNotes=qualificationNotes;this.supportingInformation=supportingInformation;
        if(status==SqlQualificationStatus.REJECTED){status=SqlQualificationStatus.DRAFT;rejectionReason=null;reviewedBy=null;reviewedAt=null;submittedAt=null;}
    }
    public void submit(Instant now){status=SqlQualificationStatus.SUBMITTED;submittedAt=now;rejectionReason=null;reviewedBy=null;reviewedAt=null;}
    public void approve(UserEntity reviewer,Instant now){status=SqlQualificationStatus.APPROVED;reviewedBy=reviewer;reviewedAt=now;rejectionReason=null;}
    public void reject(UserEntity reviewer,Instant now,String reason){status=SqlQualificationStatus.REJECTED;reviewedBy=reviewer;reviewedAt=now;rejectionReason=reason;}
    public UUID getId(){return id;} public LeadEntity getLead(){return lead;} public UserEntity getCreatedBy(){return createdBy;} public UserEntity getReviewedBy(){return reviewedBy;} public String getCompanySize(){return companySize;} public String getIndustry(){return industry;} public String getBusinessRequirement(){return businessRequirement;} public Boolean getDecisionMakerIdentified(){return decisionMakerIdentified;} public String getDecisionMakerDetails(){return decisionMakerDetails;} public BigDecimal getEstimatedDealValue(){return estimatedDealValue;} public String getExpectedTimeline(){return expectedTimeline;} public String getQualificationNotes(){return qualificationNotes;} public String getSupportingInformation(){return supportingInformation;} public SqlQualificationStatus getStatus(){return status;} public Instant getSubmittedAt(){return submittedAt;} public Instant getReviewedAt(){return reviewedAt;} public String getRejectionReason(){return rejectionReason;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
}
