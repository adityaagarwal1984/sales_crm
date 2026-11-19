package com.prosperr.crm.prospecting.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prosperr.crm.identity.domain.UserEntity;
import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.identity.repository.UserRepository;
import com.prosperr.crm.organisation.domain.ContactEntity;
import com.prosperr.crm.organisation.domain.OrganisationEntity;
import com.prosperr.crm.organisation.repository.ContactRepository;
import com.prosperr.crm.organisation.repository.OrganisationRepository;
import com.prosperr.crm.prospecting.api.ProspectingDtos.*;
import com.prosperr.crm.prospecting.domain.*;
import com.prosperr.crm.prospecting.repository.*;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SqlQualificationService {
    private final SqlQualificationRepository qualifications;
    private final SqlQualificationHistoryRepository history;
    private final LeadRepository leads;
    private final OrganisationRepository organisations;
    private final ContactRepository contacts;
    private final UserRepository users;
    private final ObjectMapper objectMapper;

    public SqlQualificationService(SqlQualificationRepository qualifications,SqlQualificationHistoryRepository history,LeadRepository leads,OrganisationRepository organisations,ContactRepository contacts,UserRepository users,ObjectMapper objectMapper){this.qualifications=qualifications;this.history=history;this.leads=leads;this.organisations=organisations;this.contacts=contacts;this.users=users;this.objectMapper=objectMapper;}

    @Transactional(readOnly=true)
    public Page<SqlQualificationView> listBdr(CrmPrincipal actor,int page,int size){return qualifications.findByCreatedBy_IdOrderByUpdatedAtDesc(actor.id(),pageable(page,size)).map(this::view);}

    @Transactional
    public SqlQualificationDetail createDraft(CrmPrincipal actor,SqlQualificationInput input){
        LeadEntity lead=ownedLead(actor,input.leadId());validateLinkedRecords(lead,input.organisationId(),input.primaryContactId());
        if(lead.getLastCallOutcome()!=CallOutcome.INTERESTED)throw new ResponseStatusException(HttpStatus.CONFLICT,"Only an interested prospect can be qualified");
        if(qualifications.existsByLead_Id(lead.getId()))throw new ResponseStatusException(HttpStatus.CONFLICT,"A qualification already exists for this sales pursuit");
        SqlQualificationEntity qualification=new SqlQualificationEntity(lead,user(actor.id()));apply(qualification,input);qualifications.save(qualification);record(qualification,actor,"DRAFT_CREATED",null,SqlQualificationStatus.DRAFT,null,input);return detail(qualification);
    }

    @Transactional(readOnly=true)
    public SqlQualificationDetail getBdr(CrmPrincipal actor,UUID id){return detail(ownedQualification(actor,id));}

    @Transactional(readOnly=true)
    public SqlQualificationDetail getBdrByLead(CrmPrincipal actor,UUID leadId){ownedLead(actor,leadId);SqlQualificationEntity qualification=qualifications.findByLead_IdAndCreatedBy_Id(leadId,actor.id()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Qualification not found"));return detail(qualification);}

    @Transactional
    public SqlQualificationDetail saveDraft(CrmPrincipal actor,UUID id,SqlQualificationInput input){
        SqlQualificationEntity qualification=ownedQualification(actor,id);LeadEntity lead=qualification.getLead();if(!lead.getId().equals(input.leadId()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Qualification cannot be moved to another lead");validateLinkedRecords(lead,input.organisationId(),input.primaryContactId());
        if(qualification.getStatus()!=SqlQualificationStatus.DRAFT&&qualification.getStatus()!=SqlQualificationStatus.REJECTED)throw new ResponseStatusException(HttpStatus.CONFLICT,"Only draft or rejected qualifications can be edited");
        SqlQualificationStatus previous=qualification.getStatus();apply(qualification,input);record(qualification,actor,"DRAFT_SAVED",previous,qualification.getStatus(),null,input);return detail(qualification);
    }

    @Transactional
    public SqlQualificationDetail submit(CrmPrincipal actor,UUID id){SqlQualificationEntity qualification=ownedQualification(actor,id);if(qualification.getStatus()!=SqlQualificationStatus.DRAFT)throw new ResponseStatusException(HttpStatus.CONFLICT,"Only a saved draft can be submitted");validateForSubmit(qualification);SqlQualificationStatus previous=qualification.getStatus();qualification.submit(Instant.now());record(qualification,actor,"SUBMITTED",previous,qualification.getStatus(),null,snapshot(qualification));return detail(qualification);}

    @Transactional(readOnly=true)
    public Page<SqlQualificationView> managerQueue(CrmPrincipal actor,String filter,int page,int size){Pageable pageable=pageable(page,size);if(filter==null||filter.isBlank())filter="SUBMITTED";try{SqlQualificationStatus status=SqlQualificationStatus.valueOf(filter.trim().toUpperCase(Locale.ROOT));if(status==SqlQualificationStatus.DRAFT)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Draft qualifications are private to their BDR");return qualifications.findByStatusOrderBySubmittedAtDesc(status,pageable).map(this::view);}catch(IllegalArgumentException e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Status must be SUBMITTED, APPROVED or REJECTED");}}

    @Transactional(readOnly=true)
    public SqlQualificationDetail getForManager(CrmPrincipal actor,UUID id){return detail(managerQualification(actor,id));}

    @Transactional(readOnly=true)
    public Page<SqlQualificationView> unassignedApprovedForManager(CrmPrincipal actor,int page,int size){return qualifications.findUnassignedByStatus(SqlQualificationStatus.APPROVED,pageable(page,size)).map(this::view);}

    @Transactional
    public SqlQualificationDetail approve(CrmPrincipal actor,UUID id){SqlQualificationEntity qualification=managerReviewTarget(actor,id);SqlQualificationStatus previous=qualification.getStatus();UserEntity reviewer=user(actor.id());qualification.approve(reviewer,Instant.now());record(qualification,actor,"APPROVED",previous,qualification.getStatus(),null,snapshot(qualification));return detail(qualification);}

    @Transactional
    public SqlQualificationDetail reject(CrmPrincipal actor,UUID id,String reason){SqlQualificationEntity qualification=managerReviewTarget(actor,id);String cleaned=clean(reason);if(cleaned==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"A rejection reason is required");SqlQualificationStatus previous=qualification.getStatus();qualification.reject(user(actor.id()),Instant.now(),cleaned);record(qualification,actor,"REJECTED",previous,qualification.getStatus(),cleaned,snapshot(qualification));return detail(qualification);}

    private SqlQualificationEntity managerReviewTarget(CrmPrincipal actor,UUID id){SqlQualificationEntity qualification=managerQualification(actor,id);if(qualification.getStatus()!=SqlQualificationStatus.SUBMITTED)throw new ResponseStatusException(HttpStatus.CONFLICT,"Only submitted qualifications can be reviewed");return qualification;}
    private SqlQualificationEntity managerQualification(CrmPrincipal actor,UUID id){return qualifications.findById(id).filter(q -> q.getCreatedBy().getRole().getCode() == com.prosperr.crm.identity.domain.RoleCode.BDR).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Qualification not found"));}
    private void validateForSubmit(SqlQualificationEntity q){if(clean(q.getBusinessRequirement())==null)bad("Business requirement is required");if(q.getDecisionMakerIdentified()==null)bad("Select whether a decision-maker has been identified");if(Boolean.TRUE.equals(q.getDecisionMakerIdentified())&&clean(q.getDecisionMakerDetails())==null)bad("Decision-maker details are required when identified");if(q.getEstimatedDealValue()==null)bad("Estimated deal value is required");if(clean(q.getExpectedTimeline())==null)bad("Expected timeline is required");}
    private void bad(String message){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,message);}
    private void validateLinkedRecords(LeadEntity lead,UUID organisationId,UUID contactId){if(!lead.getOrganisation().getId().equals(organisationId))bad("Qualification organisation must match the selected lead");if(!lead.getPrimaryContact().getId().equals(contactId))bad("Qualification primary contact must match the selected lead");OrganisationEntity organisation=organisations.findById(organisationId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Organisation not found"));ContactEntity contact=contacts.findById(contactId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not found"));if(!organisation.getId().equals(contact.getOrganisation().getId()))bad("Primary contact must belong to the selected organisation");}
    private void apply(SqlQualificationEntity q,SqlQualificationInput input){q.saveDraft(clean(input.companySize()),clean(input.industry()),clean(input.businessRequirement()),input.decisionMakerIdentified(),clean(input.decisionMakerDetails()),input.estimatedDealValue(),clean(input.expectedTimeline()),clean(input.qualificationNotes()),clean(input.supportingInformation()));}
    private QualificationSnapshot snapshot(SqlQualificationEntity q){return new QualificationSnapshot(q.getId(),q.getLead().getId(),q.getLead().getOrganisation().getId(),q.getLead().getPrimaryContact().getId(),q.getCompanySize(),q.getIndustry(),q.getBusinessRequirement(),q.getDecisionMakerIdentified(),q.getDecisionMakerDetails(),q.getEstimatedDealValue(),q.getExpectedTimeline(),q.getQualificationNotes(),q.getSupportingInformation(),q.getStatus().name(),q.getRejectionReason());}
    private void record(SqlQualificationEntity q,CrmPrincipal actor,String action,SqlQualificationStatus from,SqlQualificationStatus to,String reason,Object snapshot){try{history.save(new SqlQualificationHistoryEntity(q,user(actor.id()),action,from==null?null:from.name(),to.name(),reason,objectMapper.writeValueAsString(snapshot)));}catch(JsonProcessingException e){throw new IllegalStateException("Could not capture SQL qualification history",e);}}
    private SqlQualificationDetail detail(SqlQualificationEntity q){List<SqlQualificationHistoryView> events=history.findByQualification_IdOrderByCreatedAtDesc(q.getId()).stream().map(e->new SqlQualificationHistoryView(e.getId(),e.getActorId(),e.getActorName(),e.getAction(),e.getFromStatus(),e.getToStatus(),e.getReason(),e.getSnapshotJson(),e.getCreatedAt())).toList();return new SqlQualificationDetail(view(q),events);}
    private SqlQualificationView view(SqlQualificationEntity q){LeadEntity lead=q.getLead();return new SqlQualificationView(q.getId(),lead.getId(),lead.getTitle(),lead.getDescription(),lead.getOrganisation().getId(),lead.getOrganisation().getName(),lead.getPrimaryContact().getId(),lead.getPrimaryContact().getName(),lead.getPrimaryContact().getEmail(),q.getCompanySize(),q.getIndustry(),q.getBusinessRequirement(),q.getDecisionMakerIdentified(),q.getDecisionMakerDetails(),q.getEstimatedDealValue(),q.getExpectedTimeline(),q.getQualificationNotes(),q.getSupportingInformation(),q.getStatus().name(),q.getCreatedBy().getId(),q.getCreatedBy().getFullName(),q.getCreatedAt(),q.getUpdatedAt(),q.getSubmittedAt(),q.getReviewedBy()==null?null:q.getReviewedBy().getId(),q.getReviewedBy()==null?null:q.getReviewedBy().getFullName(),q.getReviewedAt(),q.getRejectionReason(),q.getStatus()==SqlQualificationStatus.APPROVED);}
    private SqlQualificationEntity ownedQualification(CrmPrincipal actor,UUID id){return qualifications.findByIdAndCreatedBy_Id(id,actor.id()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Qualification not found"));}
    private LeadEntity ownedLead(CrmPrincipal actor,UUID id){return leads.findByIdAndOwner_Id(id,actor.id()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Lead not found"));}
    private UserEntity user(UUID id){return users.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not found"));}
    private Pageable pageable(int page,int size){if(page<0||size<1||size>100)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Page must be non-negative and size must be between 1 and 100");return PageRequest.of(page,size);}
    private static String clean(String value){if(value==null)return null;String cleaned=value.trim();return cleaned.isEmpty()?null:cleaned;}
    private record QualificationSnapshot(UUID qualificationId,UUID leadId,UUID organisationId,UUID primaryContactId,String companySize,String industry,String businessRequirement,Boolean decisionMakerIdentified,String decisionMakerDetails,java.math.BigDecimal estimatedDealValue,String expectedTimeline,String qualificationNotes,String supportingInformation,String status,String rejectionReason){}
}
