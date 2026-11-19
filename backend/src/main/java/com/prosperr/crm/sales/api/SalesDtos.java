package com.prosperr.crm.sales.api;

import com.prosperr.crm.sales.domain.OpportunityStatus;
import com.prosperr.crm.sales.domain.OpportunityMeetingType;
import com.prosperr.crm.sales.domain.OpportunityMeetingLevel;
import com.prosperr.crm.sales.domain.OpportunityStakeholderRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class SalesDtos {
    private SalesDtos() {}

    public record AssignOpportunityInput(@NotNull UUID aeId) {}
    public record OutcomeInput(@NotNull OpportunityStatus status) {}
    public record CompletedMeetingInput(@NotBlank @Size(max = 180) String title,
            @Size(max = 10000) String notes, @NotNull Instant completedAt,
            OpportunityMeetingType meetingType, OpportunityMeetingLevel meetingLevel,
            @NotBlank @Size(max = 5000) String outcome, @Size(max = 5000) String nextAction,
            Instant followUpAt, List<UUID> participantIds) {}
    public record StageTransitionInput(@NotNull OpportunityStatus stage, @Size(max = 2000) String reason) {}
    public record CloseWonInput(@NotNull @jakarta.validation.constraints.DecimalMin(value="0.01") BigDecimal finalDealValue,
            @NotNull Instant closedAt, @NotBlank @Size(max=10000) String commercialDetails) {}
    public record CloseLostInput(@NotNull Instant closedAt, @NotBlank @Size(max=500) String lostReason,
            @Size(max=10000) String closureNotes) {}
    public record StakeholderInput(@NotNull UUID contactId, @NotNull OpportunityStakeholderRole role) {}
    public record ReopenRequestInput(@NotBlank @Size(max=2000) String reason) {}
    public record ReopenDecisionInput(@NotNull Boolean approved, @Size(max=2000) String reason) {}

    public record ExecutiveView(UUID id, String fullName, String email, boolean active) {}
    public record OpportunityView(UUID id, UUID qualificationId, UUID leadId, String leadTitle,
            UUID organisationId, String organisationName, String primaryContactName,
            UUID assignedAeId, String assignedAeName, String status, BigDecimal amount,
            String currencyCode, Instant assignedAt, boolean active) {}
    public record AssignmentHistoryView(UUID id, UUID opportunityId, UUID previousAeId,
            String previousAeName, UUID assignedAeId, String assignedAeName, UUID assignedById,
            String assignedByName, String action, Instant createdAt) {}
    public record MeetingView(UUID id, UUID opportunityId, UUID aeId, String title, String notes,
            Instant completedAt, String meetingType, String meetingLevel, List<ContactView> participants,
            String outcome, String nextAction, Instant followUpAt, Instant followUpCompletedAt) {}
    public record ContactView(UUID id, String name, String email, String phone, String designation, String department) {}
    public record StakeholderView(UUID id, UUID contactId, String contactName, String email, String phone,
            String designation, String department, String role, Instant createdAt) {}
    public record StageHistoryView(UUID id, String fromStage, String toStage, String reason,
            UUID actorId, String actorName, Instant createdAt) {}
    public record ClosureView(UUID id, String outcome, BigDecimal finalDealValue, String currencyCode,
            Instant closedAt, String commercialDetails, String lostReason, String closureNotes,
            String closedByName) {}
    public record HandoverView(UUID id, String status, Instant createdAt, BigDecimal finalDealValue,
            String currencyCode, String commercialDetails) {}
    public record OpportunityDetailView(OpportunityView opportunity, OrganisationView organisation,
            List<ContactView> contacts, QualificationView qualification,
            List<AssignmentHistoryView> assignmentHistory, List<StageHistoryView> stageHistory,
            List<StakeholderView> stakeholders, List<MeetingView> meetings,
            List<ClosureView> closures, List<HandoverView> handovers) {}
    public record OrganisationView(UUID id, String name, String website, String industry,
            String companySize, String location, String address, String leadSource) {}
    public record QualificationView(UUID id, String status, String companySize, String industry,
            String businessRequirement, Boolean decisionMakerIdentified, String decisionMakerDetails,
            BigDecimal estimatedDealValue, String expectedTimeline, String qualificationNotes,
            String supportingInformation, String bdrName, Instant submittedAt, Instant reviewedAt) {}
    public record ReopenRequestView(UUID id, UUID opportunityId, String leadTitle, String organisationName,
            String aeName, String reason, String status, Instant createdAt) {}
    public record AeDashboardView(long assignedOpportunities, long l1Meetings, long l2Meetings,
            long l3Meetings, long pendingFollowUps, long wonDeals, long lostDeals,
            BigDecimal totalPipelineValue, BigDecimal revenueGenerated, String currencyCode) {}
    public record AnalyticsFilter(LocalDate fromDate, LocalDate toDate, Integer year, Integer month, Integer quarter,
            String department, UUID employeeId, String stage) {}
    public record LeadershipOverviewView(long totalOrganisations, long totalContacts, long activeLeads,
            long totalSqls, long totalOpportunities, long totalWonCustomers, long totalLostOpportunities,
            BigDecimal totalRevenue, String currencyCode) {}
    public record BdrAnalyticsView(UUID userId, String fullName, long organisationsDiscovered,
            long contactsCollected, long callsCompleted, long interestedProspects, long sqlsGenerated,
            long approvedSqls, BigDecimal sqlConversionPercent) {}
    public record ManagerAnalyticsView(UUID userId, String fullName, long sqlsReviewed,
            long sqlsApproved, BigDecimal sqlApprovalRate, BigDecimal averageAssignmentHours,
            long opportunitiesAllocated, Long expiredAccountRevivalPerformance) {}
    public record AeAnalyticsView(UUID userId, String fullName, long totalAssigned, long l1, long l2, long l3,
            long won, long lost, BigDecimal conversionPercent, BigDecimal revenue, String currencyCode) {}
    public record FunnelStepView(String stage, long count) {}
    public record RevenuePointView(String period, BigDecimal revenue) {}
    public record RevenueByAeView(UUID userId, String fullName, long dealsWon, BigDecimal revenue) {}
    public record RevenueBySourceView(String source, long dealsWon, BigDecimal revenue) {}
    public record LeadershipRevenueView(List<RevenuePointView> monthly, List<RevenuePointView> quarterly,
            List<RevenueByAeView> byAe, List<RevenueBySourceView> bySource, String currencyCode) {}
    public record AnalyticsEmployeeView(UUID id, String fullName, String role) {}
    public record LeadershipFilterOptions(List<AnalyticsEmployeeView> employees, List<String> departments,
            List<String> opportunityStages) {}
    public record BdrPerformanceView(UUID userId, String fullName, long organisationsDiscovered,
            long contactsAdded, long callsCompleted, long sqlsGenerated) {}
    public record AePerformanceView(UUID userId, String fullName, long opportunitiesAssigned,
            long meetingsCompleted, long dealsWon, BigDecimal revenueGenerated, String currencyCode) {}
    public record TeamPerformanceView(List<BdrPerformanceView> bdrs, List<AePerformanceView> aes) {}
    public record SalesManagerDashboardView(long totalBdrs, long totalAes, long sqlsPendingReview,
            long sqlsApproved, long opportunitiesAssigned, long activeOpportunities,
            Long expiredCustomerAccounts, BigDecimal teamConversionRate) {}
    public record NotificationView(UUID id, String title, String message, String targetPath,
            Instant createdAt, Instant readAt) {}
    public record NotificationList(List<NotificationView> items, long unreadCount) {}
}
