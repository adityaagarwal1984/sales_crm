package com.prosperr.crm.sales.service;

import com.prosperr.crm.identity.domain.RoleCode;
import com.prosperr.crm.identity.domain.UserEntity;
import com.prosperr.crm.identity.repository.UserRepository;
import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.organisation.repository.ContactRepository;
import com.prosperr.crm.organisation.repository.OrganisationRepository;
import com.prosperr.crm.prospecting.domain.SqlQualificationEntity;
import com.prosperr.crm.prospecting.domain.SqlQualificationStatus;
import com.prosperr.crm.prospecting.repository.CallActivityRepository;
import com.prosperr.crm.prospecting.repository.SqlQualificationRepository;
import com.prosperr.crm.sales.api.SalesDtos.*;
import com.prosperr.crm.sales.domain.*;
import com.prosperr.crm.sales.repository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class OpportunityService {
    private static final String DEFAULT_CURRENCY = "INR";
    private final UserRepository users;
    private final SqlQualificationRepository qualifications;
    private final OpportunityRepository opportunities;
    private final OpportunityAssignmentHistoryRepository assignmentHistory;
    private final OpportunityStageHistoryRepository stageHistory;
    private final OpportunityMeetingRepository meetings;
    private final OrganisationRepository organisations;
    private final ContactRepository contacts;
    private final CallActivityRepository calls;
    private final AppNotificationService notifications;

    public OpportunityService(UserRepository users, SqlQualificationRepository qualifications,
            OpportunityRepository opportunities, OpportunityAssignmentHistoryRepository assignmentHistory,
            OpportunityStageHistoryRepository stageHistory,
            OpportunityMeetingRepository meetings, OrganisationRepository organisations,
            ContactRepository contacts, CallActivityRepository calls, AppNotificationService notifications) {
        this.users = users;
        this.qualifications = qualifications;
        this.opportunities = opportunities;
        this.assignmentHistory = assignmentHistory;
        this.stageHistory = stageHistory;
        this.meetings = meetings;
        this.organisations = organisations;
        this.contacts = contacts;
        this.calls = calls;
        this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public SalesManagerDashboardView dashboard(CrmPrincipal manager) {
        long assigned = opportunities.count();
        long won = opportunities.countByStatus(OpportunityStatus.WON);
        BigDecimal conversion = assigned == 0 ? BigDecimal.ZERO
                : BigDecimal.valueOf(won).multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(assigned), 1, RoundingMode.HALF_UP);
        return new SalesManagerDashboardView(
                users.countByRole_Code(RoleCode.BDR),
                users.countByRole_Code(RoleCode.AE),
                qualifications.countByStatus(SqlQualificationStatus.SUBMITTED),
                qualifications.countUnassignedByStatus(SqlQualificationStatus.APPROVED),
                assigned,
                openOpportunityCount(),
                null,
                conversion);
    }

    @Transactional(readOnly = true)
    public TeamPerformanceView teamPerformance(CrmPrincipal manager) {
        List<BdrPerformanceView> bdrs = users.findAllByRole_CodeOrderByFullNameAsc(RoleCode.BDR)
                .stream().map(bdr -> new BdrPerformanceView(bdr.getId(), bdr.getFullName(),
                        organisations.countByCreatedBy_Id(bdr.getId()),
                        contacts.countByCreatedBy_Id(bdr.getId()),
                        calls.countByCreatedBy_Id(bdr.getId()),
                        qualifications.countByCreatedBy_IdAndStatusNot(bdr.getId(), SqlQualificationStatus.DRAFT)))
                .toList();

        List<AePerformanceView> aes = users.findAllByRole_CodeOrderByFullNameAsc(RoleCode.AE)
                .stream().map(ae -> new AePerformanceView(ae.getId(), ae.getFullName(),
                        opportunities.countByAssignedAe_Id(ae.getId()),
                        meetings.countByAeUser_Id(ae.getId()),
                        opportunities.countByAssignedAe_IdAndStatus(ae.getId(), OpportunityStatus.WON),
                        opportunities.sumFinalRevenueByAe(ae.getId(), OpportunityStatus.WON),
                        DEFAULT_CURRENCY))
                .toList();
        return new TeamPerformanceView(bdrs, aes);
    }

    private long openOpportunityCount() {
        return opportunities.countByStatus(OpportunityStatus.L1)
                + opportunities.countByStatus(OpportunityStatus.L2)
                + opportunities.countByStatus(OpportunityStatus.L3);
    }

    @Transactional(readOnly = true)
    public List<ExecutiveView> activeAes(CrmPrincipal manager) {
        return users.findAllByRole_CodeAndActiveTrueOrderByFullNameAsc(RoleCode.AE).stream()
                .filter(UserEntity::isActive)
                .map(ae -> new ExecutiveView(ae.getId(), ae.getFullName(), ae.getEmail(), ae.isActive()))
                .toList();
    }

    @Transactional
    public OpportunityView assignApprovedSql(CrmPrincipal manager, UUID qualificationId, UUID aeId) {
        SqlQualificationEntity qualification = qualifications.findById(qualificationId)
                .orElseThrow(() -> notFound("Approved SQL not found"));
        requireBdrQualification(qualification);
        if (qualification.getStatus() != SqlQualificationStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only approved SQL qualifications can be assigned");
        }
        UserEntity ae = activeTeamAe(manager, aeId);
        Instant now = Instant.now();
        OpportunityEntity opportunity = opportunities.findByQualification_Id(qualificationId).orElse(null);
        String action;
        UserEntity previousAe = null;
        if (opportunity == null) {
            BigDecimal amount = qualification.getEstimatedDealValue();
            if (amount == null) throw new ResponseStatusException(HttpStatus.CONFLICT, "Approved SQL is missing an estimated deal value");
            opportunity = opportunities.save(new OpportunityEntity(qualification, ae, user(manager.id()), amount, DEFAULT_CURRENCY, now));
            stageHistory.save(new OpportunityStageHistoryEntity(opportunity, user(manager.id()), null, OpportunityStatus.L1.name(), "Created from approved SQL"));
            action = "ASSIGNED";
        } else {
            if (!opportunity.getStatus().isOpen()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Only active opportunities can be reassigned");
            }
            previousAe = opportunity.getAssignedAe();
            if (previousAe.getId().equals(ae.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This opportunity is already assigned to that AE");
            }
            opportunity.reassign(ae, now);
            action = "REASSIGNED";
        }
        assignmentHistory.save(new OpportunityAssignmentHistoryEntity(opportunity, previousAe, ae, user(manager.id()), action));
        String title = action.equals("ASSIGNED") ? "New opportunity assigned" : "Opportunity reassigned to you";
        String message = opportunity.getQualification().getLead().getTitle() + " · "
                + opportunity.getQualification().getLead().getOrganisation().getName();
        notifications.notifyOpportunityAssigned(ae, title, message);
        return view(opportunity);
    }

    @Transactional(readOnly = true)
    public Page<OpportunityView> managerOpportunities(CrmPrincipal manager, int page, int size) {
        return opportunities.findAllByOrderByAssignedAtDesc(pageable(page, size))
                .map(this::view);
    }

    @Transactional
    public OpportunityView reassign(CrmPrincipal manager, UUID opportunityId, UUID aeId) {
        OpportunityEntity opportunity = managerOpportunity(manager, opportunityId);
        if (!opportunity.getStatus().isOpen()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only active opportunities can be reassigned");
        }
        UserEntity nextAe = activeTeamAe(manager, aeId);
        UserEntity previousAe = opportunity.getAssignedAe();
        if (previousAe.getId().equals(nextAe.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This opportunity is already assigned to that AE");
        }
        opportunity.reassign(nextAe, Instant.now());
        assignmentHistory.save(new OpportunityAssignmentHistoryEntity(opportunity, previousAe, nextAe,
                user(manager.id()), "REASSIGNED"));
        notifications.notifyOpportunityAssigned(nextAe, "Opportunity reassigned to you",
                opportunity.getQualification().getLead().getTitle() + " · "
                        + opportunity.getQualification().getLead().getOrganisation().getName());
        return view(opportunity);
    }

    @Transactional(readOnly = true)
    public List<AssignmentHistoryView> assignmentHistory(CrmPrincipal manager, UUID opportunityId) {
        OpportunityEntity opportunity = managerOpportunity(manager, opportunityId);
        return assignmentHistory.findByOpportunity_IdOrderByCreatedAtDesc(opportunity.getId()).stream()
                .map(event -> new AssignmentHistoryView(event.getId(), opportunity.getId(),
                        event.getPreviousAe() == null ? null : event.getPreviousAe().getId(),
                        event.getPreviousAe() == null ? null : event.getPreviousAe().getFullName(),
                        event.getAssignedAe().getId(), event.getAssignedAe().getFullName(),
                        event.getAssignedBy().getId(), event.getAssignedBy().getFullName(),
                        event.getAction(), event.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<OpportunityView> aeOpportunities(CrmPrincipal ae, int page, int size) {
        return opportunities.findByAssignedAe_IdOrderByAssignedAtDesc(ae.id(), pageable(page, size)).map(this::view);
    }

    private OpportunityEntity managerOpportunity(CrmPrincipal manager, UUID id) {
        return opportunities.findById(id)
                .orElseThrow(() -> notFound("Opportunity not found"));
    }

    private void requireBdrQualification(SqlQualificationEntity qualification) {
        if (qualification.getCreatedBy().getRole().getCode() != RoleCode.BDR) throw notFound("Approved SQL not found");
    }

    private UserEntity activeTeamAe(CrmPrincipal manager, UUID aeId) {
        UserEntity ae = users.findById(aeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "AE not found"));
        if (!ae.isActive() || ae.getRole().getCode() != RoleCode.AE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose an active AE account");
        }
        return ae;
    }

    private OpportunityView view(OpportunityEntity opportunity) {
        var lead = opportunity.getQualification().getLead();
        return new OpportunityView(opportunity.getId(), opportunity.getQualification().getId(), lead.getId(),
                lead.getTitle(), lead.getOrganisation().getId(), lead.getOrganisation().getName(),
                lead.getPrimaryContact().getName(), opportunity.getAssignedAe().getId(),
                opportunity.getAssignedAe().getFullName(), opportunity.getStatus().name(),
                opportunity.getAmount(), opportunity.getCurrencyCode(), opportunity.getAssignedAt(),
                opportunity.getStatus().isOpen());
    }

    private UserEntity user(UUID id) {
        return users.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Pageable pageable(int page, int size) {
        if (page < 0 || size < 1 || size > 100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must be non-negative and size must be between 1 and 100");
        return PageRequest.of(page, size);
    }

    private ResponseStatusException notFound(String message) { return new ResponseStatusException(HttpStatus.NOT_FOUND, message); }
    private String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
