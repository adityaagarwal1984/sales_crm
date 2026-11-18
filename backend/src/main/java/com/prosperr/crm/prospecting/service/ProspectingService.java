package com.prosperr.crm.prospecting.service;

import com.prosperr.crm.identity.domain.UserEntity;
import com.prosperr.crm.identity.domain.RoleCode;
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
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProspectingService {
    private final LeadRepository leads; private final CallActivityRepository calls; private final FollowUpRepository followUps; private final SqlSubmissionRepository sqls;
    private final OrganisationRepository organisations; private final ContactRepository contacts; private final UserRepository users; private final SqlQualificationRepository sqlQualifications;

    public ProspectingService(LeadRepository leads,CallActivityRepository calls,FollowUpRepository followUps,SqlSubmissionRepository sqls,OrganisationRepository organisations,ContactRepository contacts,UserRepository users,SqlQualificationRepository sqlQualifications){this.leads=leads;this.calls=calls;this.followUps=followUps;this.sqls=sqls;this.organisations=organisations;this.contacts=contacts;this.users=users;this.sqlQualifications=sqlQualifications;}

    @Transactional(readOnly=true)
    public Page<LeadView> listLeads(CrmPrincipal principal,String search,int page,int size){Pageable pageable=pageable(page,size,Sort.by(Sort.Direction.DESC,"createdAt"));String normalized=clean(search);Page<LeadEntity> result=normalized==null?leads.findByOwner_IdOrderByCreatedAtDesc(principal.id(),pageable):leads.searchByOwner(principal.id(),"%"+normalized.toLowerCase(Locale.ROOT)+"%",pageable);return result.map(this::leadView);}
    @Transactional(readOnly=true)
    public LeadView getLead(CrmPrincipal principal,UUID id){return leadView(lead(principal,id));}
    @Transactional
    public LeadView createLead(CrmPrincipal principal,LeadInput input){UserEntity owner=user(principal.id());OrganisationEntity organisation=organisation(input.organisationId());ContactEntity contact=contact(input.primaryContactId());sameOrganisation(organisation,contact);LeadEntity saved=leads.save(new LeadEntity(input.title().trim(),clean(input.description()),organisation,contact,owner,owner));return leadView(saved);}
    @Transactional
    public LeadView updateLead(CrmPrincipal principal,UUID id,LeadInput input){LeadEntity lead=lead(principal,id);OrganisationEntity organisation=organisation(input.organisationId());ContactEntity contact=contact(input.primaryContactId());sameOrganisation(organisation,contact);lead.update(input.title().trim(),clean(input.description()),organisation,contact);return leadView(lead);}

    @Transactional
    public CallResult recordCall(CrmPrincipal principal,UUID leadId,CallInput input){LeadEntity lead=lead(principal,leadId);ContactEntity contact=contact(input.contactId());sameOrganisation(lead.getOrganisation(),contact);validateNextFollowUp(input.calledAt(),input.nextFollowUpAt());UserEntity actor=user(principal.id());CallActivityEntity call=calls.save(new CallActivityEntity(lead,contact,actor,input.calledAt(),input.outcome(),clean(input.notes()),input.nextFollowUpAt()));lead.recordLatestCall(input.outcome(),input.calledAt());FollowUpEntity createdFollowUp=input.nextFollowUpAt()==null?null:followUps.save(new FollowUpEntity(lead,contact,call,lead.getOwner(),actor,input.nextFollowUpAt()));return new CallResult(callView(call),createdFollowUp==null?null:followUpView(createdFollowUp));}
    @Transactional(readOnly=true)
    public Page<CallView> callsForLead(CrmPrincipal principal,UUID leadId,int page,int size){lead(principal,leadId);return calls.findByLead_IdAndLead_Owner_IdOrderByCalledAtDesc(leadId,principal.id(),pageable(page,size,Sort.unsorted())).map(this::callView);}
    @Transactional(readOnly=true)
    public Page<CallView> allCalls(CrmPrincipal principal,int page,int size){return calls.findByLead_Owner_IdOrderByCalledAtDesc(principal.id(),pageable(page,size,Sort.unsorted())).map(this::callView);}

    @Transactional(readOnly=true)
    public Page<FollowUpView> listFollowUps(CrmPrincipal principal,String filter,int page,int size){Pageable pageable=pageable(page,size,Sort.unsorted());Instant now=Instant.now();String selected=filter==null||filter.isBlank()?"UPCOMING":filter.trim().toUpperCase(Locale.ROOT);return switch(selected){case "UPCOMING"->followUps.findByOwner_IdAndStatusAndDueAtGreaterThanEqualOrderByDueAtAsc(principal.id(),FollowUpStatus.PENDING,now,pageable).map(this::followUpView);case "OVERDUE"->followUps.findByOwner_IdAndStatusAndDueAtBeforeOrderByDueAtAsc(principal.id(),FollowUpStatus.PENDING,now,pageable).map(this::followUpView);case "COMPLETED"->followUps.findByOwner_IdAndStatusOrderByDueAtDesc(principal.id(),FollowUpStatus.COMPLETED,pageable).map(this::followUpView);case "ALL"->followUps.findByOwner_IdOrderByDueAtDesc(principal.id(),pageable).map(this::followUpView);default->throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Filter must be UPCOMING, OVERDUE, COMPLETED or ALL");};}
    @Transactional
    public FollowUpView rescheduleFollowUp(CrmPrincipal principal,UUID id,Instant dueAt){if(dueAt==null||dueAt.isBefore(Instant.now()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Rescheduled time must be in the future");FollowUpEntity followUp=followUp(principal,id);try{followUp.reschedule(dueAt);}catch(IllegalStateException e){throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());}return followUpView(followUp);}
    @Transactional
    public CallResult completeFollowUp(CrmPrincipal principal,UUID id,FollowUpCallInput input){FollowUpEntity followUp=followUp(principal,id);validateNextFollowUp(input.calledAt(),input.nextFollowUpAt());if(followUp.getStatus()!=FollowUpStatus.PENDING)throw new ResponseStatusException(HttpStatus.CONFLICT,"Only pending follow-ups can be completed");UserEntity actor=user(principal.id());CallActivityEntity call=calls.save(new CallActivityEntity(followUp.getLead(),followUp.getContact(),actor,input.calledAt(),input.outcome(),clean(input.notes()),input.nextFollowUpAt()));followUp.getLead().recordLatestCall(input.outcome(),input.calledAt());followUp.complete(call,Instant.now());FollowUpEntity next=input.nextFollowUpAt()==null?null:followUps.save(new FollowUpEntity(followUp.getLead(),followUp.getContact(),call,followUp.getOwner(),actor,input.nextFollowUpAt()));return new CallResult(callView(call),next==null?null:followUpView(next));}

    @Transactional(readOnly=true)
    public Page<SqlSubmissionView> sqlSubmissions(CrmPrincipal principal,int page,int size){Pageable pageable=pageable(page,size,Sort.by(Sort.Direction.DESC,"submittedAt"));Page<SqlSubmissionEntity> submissions=principal.role()==RoleCode.SALES_MANAGER?sqls.findAllByOrderBySubmittedAtDesc(pageable):sqls.findBySubmittedBy_IdOrderBySubmittedAtDesc(principal.id(),pageable);return submissions.map(this::sqlView);}
    @Transactional(readOnly=true)
    public DashboardView dashboard(CrmPrincipal principal){Instant now=Instant.now();UUID userId=principal.id();return new DashboardView(leads.countByOwner_Id(userId),organisations.countByCreatedBy_Id(userId),contacts.countByCreatedBy_Id(userId),calls.countByCreatedBy_Id(userId),leads.countByOwner_IdAndLastCallOutcome(userId,CallOutcome.INTERESTED),sqlQualifications.countByCreatedBy_IdAndStatusNot(userId,SqlQualificationStatus.DRAFT),followUps.countByOwner_IdAndStatusAndDueAtGreaterThanEqual(userId,FollowUpStatus.PENDING,now),followUps.countByOwner_IdAndStatusAndDueAtBefore(userId,FollowUpStatus.PENDING,now));}

    private LeadEntity lead(CrmPrincipal principal,UUID id){return leads.findByIdAndOwner_Id(id,principal.id()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Lead not found"));}
    private FollowUpEntity followUp(CrmPrincipal principal,UUID id){return followUps.findByIdAndOwner_Id(id,principal.id()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Follow-up not found"));}
    private UserEntity user(UUID id){return users.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"User not found"));}
    private OrganisationEntity organisation(UUID id){return organisations.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Organisation not found"));}
    private ContactEntity contact(UUID id){return contacts.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Contact not found"));}
    private void sameOrganisation(OrganisationEntity organisation,ContactEntity contact){if(!organisation.getId().equals(contact.getOrganisation().getId()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Contact must belong to the selected organisation");}
    private void validateNextFollowUp(Instant calledAt,Instant nextAt){if(calledAt==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Call date and time are required");if(nextAt!=null&&!nextAt.isAfter(calledAt))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Next follow-up must be after the call time");}
    private LeadView leadView(LeadEntity l){return new LeadView(l.getId(),l.getTitle(),l.getDescription(),l.getOrganisation().getId(),l.getOrganisation().getName(),l.getPrimaryContact().getId(),l.getPrimaryContact().getName(),l.getPrimaryContact().getEmail(),l.getOwner().getId(),l.getOwner().getFullName(),l.getCreatedBy().getId(),l.getCreatedAt(),l.getUpdatedAt(),l.getLastCallOutcome(),l.getLastCallAt());}
    private CallView callView(CallActivityEntity c){return new CallView(c.getId(),c.getLead().getId(),c.getLead().getTitle(),c.getContact().getId(),c.getContact().getName(),c.getCreatedBy().getId(),c.getCreatedBy().getFullName(),c.getCalledAt(),c.getOutcome(),c.getNotes(),c.getNextFollowUpAt(),c.getCreatedAt());}
    private FollowUpView followUpView(FollowUpEntity f){return new FollowUpView(f.getId(),f.getLead().getId(),f.getLead().getTitle(),f.getContact().getId(),f.getContact().getName(),f.getDueAt(),f.getStatus(),f.getSourceCall().getId(),f.getSourceCall().getOutcome(),f.getCreatedAt(),f.getUpdatedAt(),f.getCompletedAt(),f.getCompletedCall()==null?null:f.getCompletedCall().getId());}
    private SqlSubmissionView sqlView(SqlSubmissionEntity s){LeadEntity lead=s.getLead();return new SqlSubmissionView(s.getId(),lead.getId(),lead.getTitle(),lead.getDescription(),lead.getOrganisation().getId(),lead.getOrganisation().getName(),lead.getPrimaryContact().getId(),lead.getPrimaryContact().getName(),lead.getPrimaryContact().getEmail(),s.getSubmittedBy().getId(),s.getSubmittedBy().getFullName(),s.getSubmittedAt(),s.getStatus());}
    private Pageable pageable(int page,int size,Sort sort){if(page<0||size<1||size>100)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Page must be non-negative and size must be between 1 and 100");return PageRequest.of(page,size,sort);}
    private static String clean(String value){if(value==null)return null;String cleaned=value.trim();return cleaned.isEmpty()?null:cleaned;}
}
