package com.prosperr.crm.sales.service;

import com.prosperr.crm.identity.domain.UserEntity;
import com.prosperr.crm.identity.repository.UserRepository;
import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.organisation.domain.ContactEntity;
import com.prosperr.crm.organisation.domain.OrganisationEntity;
import com.prosperr.crm.organisation.repository.ContactRepository;
import com.prosperr.crm.prospecting.domain.SqlQualificationEntity;
import com.prosperr.crm.sales.api.SalesDtos.*;
import com.prosperr.crm.sales.domain.*;
import com.prosperr.crm.sales.repository.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Owns the AE opportunity lifecycle, its validated transitions, and immutable audit records. */
@Service
public class OpportunityLifecycleService {
    private static final String PENDING = "PENDING";
    private final UserRepository users;
    private final ContactRepository contacts;
    private final OpportunityRepository opportunities;
    private final OpportunityMeetingRepository meetings;
    private final OpportunityAssignmentHistoryRepository assignments;
    private final OpportunityStageHistoryRepository stages;
    private final OpportunityStakeholderRepository stakeholders;
    private final OpportunityClosureRepository closures;
    private final ImplementationHandoverRepository handovers;
    private final OpportunityReopenRequestRepository reopenRequests;

    public OpportunityLifecycleService(UserRepository users, ContactRepository contacts,
            OpportunityRepository opportunities, OpportunityMeetingRepository meetings,
            OpportunityAssignmentHistoryRepository assignments, OpportunityStageHistoryRepository stages,
            OpportunityStakeholderRepository stakeholders, OpportunityClosureRepository closures,
            ImplementationHandoverRepository handovers, OpportunityReopenRequestRepository reopenRequests) {
        this.users = users; this.contacts = contacts; this.opportunities = opportunities;
        this.meetings = meetings; this.assignments = assignments; this.stages = stages;
        this.stakeholders = stakeholders; this.closures = closures; this.handovers = handovers;
        this.reopenRequests = reopenRequests;
    }

    @Transactional(readOnly = true)
    public AeDashboardView dashboard(CrmPrincipal ae) {
        UUID id = ae.id();
        return new AeDashboardView(opportunities.countByAssignedAe_Id(id),
                meetings.countByAeUser_IdAndMeetingLevel(id, OpportunityMeetingLevel.L1),
                meetings.countByAeUser_IdAndMeetingLevel(id, OpportunityMeetingLevel.L2),
                meetings.countByAeUser_IdAndMeetingLevel(id, OpportunityMeetingLevel.L3),
                meetings.countPendingFollowUps(id),
                opportunities.countByAssignedAe_IdAndStatus(id, OpportunityStatus.WON),
                opportunities.countByAssignedAe_IdAndStatus(id, OpportunityStatus.LOST),
                opportunities.sumPipelineByAe(id, List.of(OpportunityStatus.L1, OpportunityStatus.L2, OpportunityStatus.L3)),
                opportunities.sumFinalRevenueByAe(id, OpportunityStatus.WON), "INR");
    }

    @Transactional(readOnly = true)
    public OpportunityDetailView detail(CrmPrincipal ae, UUID id) {
        OpportunityEntity opportunity = owned(ae, id);
        SqlQualificationEntity q = opportunity.getQualification();
        OrganisationEntity org = q.getLead().getOrganisation();
        List<ContactView> contactViews = contacts.findAllByOrganisation_IdOrderByNameAsc(org.getId()).stream().map(this::contactView).toList();
        QualificationView qualification = new QualificationView(q.getId(), q.getStatus().name(), q.getCompanySize(), q.getIndustry(),
                q.getBusinessRequirement(), q.getDecisionMakerIdentified(), q.getDecisionMakerDetails(), q.getEstimatedDealValue(),
                q.getExpectedTimeline(), q.getQualificationNotes(), q.getSupportingInformation(), q.getCreatedBy().getFullName(), q.getSubmittedAt(), q.getReviewedAt());
        List<AssignmentHistoryView> assignmentViews = assignments.findByOpportunity_IdOrderByCreatedAtDesc(id).stream()
                .map(e -> new AssignmentHistoryView(e.getId(), id, e.getPreviousAe() == null ? null : e.getPreviousAe().getId(),
                        e.getPreviousAe() == null ? null : e.getPreviousAe().getFullName(), e.getAssignedAe().getId(),
                        e.getAssignedAe().getFullName(), e.getAssignedBy().getId(), e.getAssignedBy().getFullName(), e.getAction(), e.getCreatedAt())).toList();
        List<StageHistoryView> stageViews = stages.findByOpportunity_IdOrderByCreatedAtAsc(id).stream()
                .map(e -> new StageHistoryView(e.getId(), e.getFromStage(), e.getToStage(), e.getReason(), e.getActor().getId(), e.getActor().getFullName(), e.getCreatedAt())).toList();
        List<StakeholderView> stakeholderViews = stakeholders.findByOpportunity_IdOrderByCreatedAtAsc(id).stream().map(this::stakeholderView).toList();
        List<MeetingView> meetingViews = meetings.findByOpportunity_IdOrderByCompletedAtDesc(id).stream().map(this::meetingView).toList();
        List<ClosureView> closureViews = closures.findByOpportunity_IdOrderByClosedAtDesc(id).stream()
                .map(c -> new ClosureView(c.getId(), c.getOutcome(), c.getFinalDealValue(), c.getCurrencyCode(), c.getClosedAt(),
                        c.getCommercialDetails(), c.getLostReason(), c.getClosureNotes(), c.getClosedBy().getFullName())).toList();
        List<HandoverView> handoverViews = handovers.findByOpportunity_IdOrderByCreatedAtDesc(id).stream()
                .map(h -> new HandoverView(h.getId(), h.getStatus(), h.getCreatedAt(), h.getFinalDealValue(), h.getCurrencyCode(), h.getCommercialDetails())).toList();
        return new OpportunityDetailView(view(opportunity), new OrganisationView(org.getId(), org.getName(), org.getWebsite(), org.getIndustry(),
                org.getCompanySize(), org.getLocation(), org.getAddress(), org.getLeadSource()), contactViews, qualification,
                assignmentViews, stageViews, stakeholderViews, meetingViews, closureViews, handoverViews);
    }

    @Transactional
    public OpportunityView transition(CrmPrincipal ae, UUID id, StageTransitionInput input) {
        OpportunityEntity opportunity = owned(ae, id);
        OpportunityStatus current = opportunity.getStatus();
        OpportunityStatus next = input.stage();
        if (!current.isOpen() || !next.isOpen()) conflict("Use the deal closure action for WON or LOST.");
        int currentRank = rank(current), nextRank = rank(next);
        if (nextRank <= currentRank) conflict("Opportunity stages can only move forward.");
        if (nextRank - currentRank > 1 && clean(input.reason()) == null) bad("A reason is required when skipping a stage.");
        opportunity.advanceStage(next);
        stages.save(new OpportunityStageHistoryEntity(opportunity, user(ae.id()), current.name(), next.name(), clean(input.reason())));
        return view(opportunity);
    }

    @Transactional
    public MeetingView logMeeting(CrmPrincipal ae, UUID id, CompletedMeetingInput input) {
        OpportunityEntity opportunity = ownedOpen(ae, id);
        Instant now = Instant.now();
        if (input.completedAt().isAfter(now)) bad("Meeting date and time cannot be in the future.");
        if (input.followUpAt() != null && !input.followUpAt().isAfter(input.completedAt())) bad("Follow-up must be after the meeting.");
        List<UUID> participantIds = input.participantIds() == null ? List.of() : input.participantIds().stream().distinct().toList();
        List<ContactEntity> participants = participantIds.isEmpty() ? List.of() : contacts.findAllByOrganisation_IdOrderByNameAsc(opportunity.getQualification().getLead().getOrganisation().getId()).stream()
                .filter(c -> participantIds.contains(c.getId())).toList();
        if (participants.size() != participantIds.size()) bad("Meeting participants must be contacts linked to this organisation.");
        OpportunityMeetingLevel level = input.meetingLevel();
        OpportunityMeetingEntity meeting = meetings.save(new OpportunityMeetingEntity(opportunity, user(ae.id()), input.title().trim(),
                clean(input.notes()), input.completedAt(), input.meetingType(), level, input.outcome().trim(), clean(input.nextAction()), input.followUpAt(), participants));
        if (level != null && level.name().equals(opportunity.getStatus().name()) && level != OpportunityMeetingLevel.L3) {
            OpportunityStatus current = opportunity.getStatus();
            OpportunityStatus next = current == OpportunityStatus.L1 ? OpportunityStatus.L2 : OpportunityStatus.L3;
            opportunity.advanceStage(next);
            stages.save(new OpportunityStageHistoryEntity(opportunity, user(ae.id()), current.name(), next.name(), "Advanced by completed " + level + " meeting"));
        }
        return meetingView(meeting);
    }

    @Transactional
    public MeetingView completeFollowUp(CrmPrincipal ae, UUID opportunityId, UUID meetingId) {
        owned(ae, opportunityId);
        OpportunityMeetingEntity meeting = meetings.findByIdAndAeUser_Id(meetingId, ae.id()).orElseThrow(() -> notFound("Meeting not found"));
        if (!meeting.getOpportunity().getId().equals(opportunityId)) throw notFound("Meeting not found");
        if (meeting.getFollowUpAt() == null) conflict("This meeting has no follow-up to complete.");
        if (meeting.getFollowUpCompletedAt() != null) conflict("This follow-up is already complete.");
        meeting.completeFollowUp(Instant.now());
        return meetingView(meeting);
    }

    @Transactional
    public OpportunityDetailView closeWon(CrmPrincipal ae, UUID id, CloseWonInput input) {
        OpportunityEntity opportunity = owned(ae, id);
        requireReadyToClose(opportunity);
        if (input.closedAt().isAfter(Instant.now())) bad("Closure date cannot be in the future.");
        OpportunityStatus current = opportunity.getStatus();
        UserEntity actor = user(ae.id());
        String commercial = input.commercialDetails().trim();
        opportunity.closeWon(input.finalDealValue(), input.closedAt(), commercial);
        closures.save(new OpportunityClosureEntity(opportunity, actor, "WON", input.finalDealValue(), opportunity.getCurrencyCode(), input.closedAt(), commercial, null, null));
        handovers.save(new ImplementationHandoverEntity(opportunity, actor, input.finalDealValue(), opportunity.getCurrencyCode(), commercial));
        stages.save(new OpportunityStageHistoryEntity(opportunity, actor, current.name(), "WON", null));
        return detail(ae, id);
    }

    @Transactional
    public OpportunityDetailView closeLost(CrmPrincipal ae, UUID id, CloseLostInput input) {
        OpportunityEntity opportunity = owned(ae, id);
        requireReadyToClose(opportunity);
        if (input.closedAt().isAfter(Instant.now())) bad("Closure date cannot be in the future.");
        OpportunityStatus current = opportunity.getStatus();
        UserEntity actor = user(ae.id());
        String reason = input.lostReason().trim();
        String notes = clean(input.closureNotes());
        opportunity.closeLost(input.closedAt(), reason, notes);
        closures.save(new OpportunityClosureEntity(opportunity, actor, "LOST", null, opportunity.getCurrencyCode(), input.closedAt(), null, reason, notes));
        stages.save(new OpportunityStageHistoryEntity(opportunity, actor, current.name(), "LOST", reason));
        return detail(ae, id);
    }

    @Transactional
    public StakeholderView addStakeholder(CrmPrincipal ae, UUID id, StakeholderInput input) {
        OpportunityEntity opportunity = ownedOpen(ae, id);
        UUID orgId = opportunity.getQualification().getLead().getOrganisation().getId();
        ContactEntity contact = contacts.findAllByOrganisation_IdOrderByNameAsc(orgId).stream().filter(c -> c.getId().equals(input.contactId())).findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a contact linked to this organisation."));
        if (stakeholders.existsByOpportunity_IdAndContact_Id(id, contact.getId())) conflict("This contact is already an opportunity stakeholder.");
        return stakeholderView(stakeholders.save(new OpportunityStakeholderEntity(opportunity, contact, user(ae.id()), input.role())));
    }

    @Transactional
    public void removeStakeholder(CrmPrincipal ae, UUID id, UUID contactId) {
        ownedOpen(ae, id);
        OpportunityStakeholderEntity stakeholder = stakeholders.findByOpportunity_IdAndContact_Id(id, contactId).orElseThrow(() -> notFound("Stakeholder not found"));
        stakeholders.delete(stakeholder);
    }

    @Transactional
    public ReopenRequestView requestReopen(CrmPrincipal ae, UUID id, ReopenRequestInput input) {
        OpportunityEntity opportunity = owned(ae, id);
        if (opportunity.getStatus().isOpen()) conflict("Only closed opportunities can request reopening.");
        if (reopenRequests.existsByOpportunity_IdAndStatus(id, PENDING)) conflict("A reopen request is already pending.");
        OpportunityReopenRequestEntity request = reopenRequests.save(new OpportunityReopenRequestEntity(opportunity, user(ae.id()), input.reason().trim()));
        return reopenView(request);
    }

    @Transactional(readOnly = true)
    public List<ReopenRequestView> pendingReopenRequests() {
        return reopenRequests.findByStatusOrderByCreatedAtAsc(PENDING).stream().map(this::reopenView).toList();
    }

    @Transactional
    public ReopenRequestView reviewReopenRequest(CrmPrincipal manager, UUID requestId, ReopenDecisionInput input) {
        OpportunityReopenRequestEntity request = reopenRequests.findByIdAndStatus(requestId, PENDING).orElseThrow(() -> notFound("Pending reopen request not found."));
        String reason = clean(input.reason());
        if (!input.approved() && reason == null) bad("A reason is required when rejecting a reopen request.");
        request.review(user(manager.id()), input.approved(), reason, Instant.now());
        if (input.approved()) {
            OpportunityEntity opportunity = request.getOpportunity();
            OpportunityStatus previous = opportunity.getStatus();
            opportunity.reopen();
            stages.save(new OpportunityStageHistoryEntity(opportunity, user(manager.id()), previous.name(), "L3", "Reopened: " + request.getReason()));
        }
        return reopenView(request);
    }

    private void requireReadyToClose(OpportunityEntity opportunity) {
        if (opportunity.getStatus() != OpportunityStatus.L3) conflict("Move the opportunity to L3 before closing it.");
    }
    private int rank(OpportunityStatus stage) { return switch (stage) { case L1 -> 1; case L2 -> 2; case L3 -> 3; default -> 0; }; }
    private OpportunityEntity owned(CrmPrincipal ae, UUID id) { return opportunities.findByIdAndAssignedAe_Id(id, ae.id()).orElseThrow(() -> notFound("Opportunity not found.")); }
    private OpportunityEntity ownedOpen(CrmPrincipal ae, UUID id) { OpportunityEntity o = owned(ae, id); if (!o.getStatus().isOpen()) conflict("This opportunity is closed."); return o; }
    private UserEntity user(UUID id) { return users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.")); }

    private OpportunityView view(OpportunityEntity opportunity) {
        var lead = opportunity.getQualification().getLead();
        return new OpportunityView(opportunity.getId(), opportunity.getQualification().getId(), lead.getId(), lead.getTitle(),
                lead.getOrganisation().getId(), lead.getOrganisation().getName(), lead.getPrimaryContact().getName(),
                opportunity.getAssignedAe().getId(), opportunity.getAssignedAe().getFullName(), opportunity.getStatus().name(),
                opportunity.getAmount(), opportunity.getCurrencyCode(), opportunity.getAssignedAt(), opportunity.getStatus().isOpen());
    }
    private ContactView contactView(ContactEntity c) { return new ContactView(c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getDesignation(), c.getDepartment()); }
    private MeetingView meetingView(OpportunityMeetingEntity m) { return new MeetingView(m.getId(), m.getOpportunity().getId(), m.getAeUser().getId(), m.getTitle(), m.getNotes(), m.getCompletedAt(), m.getMeetingType().name(), m.getMeetingLevel() == null ? null : m.getMeetingLevel().name(), m.getParticipants().stream().map(this::contactView).toList(), m.getOutcome(), m.getNextAction(), m.getFollowUpAt(), m.getFollowUpCompletedAt()); }
    private StakeholderView stakeholderView(OpportunityStakeholderEntity s) { ContactEntity c = s.getContact(); return new StakeholderView(s.getId(), c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getDesignation(), c.getDepartment(), s.getRole().name(), s.getCreatedAt()); }
    private ReopenRequestView reopenView(OpportunityReopenRequestEntity r) { var lead = r.getOpportunity().getQualification().getLead(); return new ReopenRequestView(r.getId(), r.getOpportunity().getId(), lead.getTitle(), lead.getOrganisation().getName(), r.getOpportunity().getAssignedAe().getFullName(), r.getReason(), r.getStatus(), r.getCreatedAt()); }
    private ResponseStatusException notFound(String message) { return new ResponseStatusException(HttpStatus.NOT_FOUND, message); }
    private void bad(String message) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
    private void conflict(String message) { throw new ResponseStatusException(HttpStatus.CONFLICT, message); }
    private String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
