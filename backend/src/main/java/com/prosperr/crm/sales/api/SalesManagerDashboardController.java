package com.prosperr.crm.sales.api;

import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.prospecting.api.ProspectingDtos.PageResponse;
import com.prosperr.crm.sales.api.SalesDtos.*;
import com.prosperr.crm.sales.service.OpportunityService;
import com.prosperr.crm.sales.service.OpportunityLifecycleService;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sales-manager")
public class SalesManagerDashboardController {
    private final OpportunityService opportunities;
    private final OpportunityLifecycleService lifecycle;

    public SalesManagerDashboardController(OpportunityService opportunities, OpportunityLifecycleService lifecycle) { this.opportunities = opportunities; this.lifecycle = lifecycle; }

    @GetMapping("/dashboard")
    public SalesManagerDashboardView dashboard(@AuthenticationPrincipal CrmPrincipal manager) {
        return opportunities.dashboard(manager);
    }

    @GetMapping("/team-performance")
    public TeamPerformanceView teamPerformance(@AuthenticationPrincipal CrmPrincipal manager) {
        return opportunities.teamPerformance(manager);
    }

    @GetMapping("/account-executives")
    public List<ExecutiveView> activeAes(@AuthenticationPrincipal CrmPrincipal manager) {
        return opportunities.activeAes(manager);
    }

    @GetMapping("/opportunities")
    public PageResponse<OpportunityView> opportunities(@AuthenticationPrincipal CrmPrincipal manager,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return PageResponse.from(opportunities.managerOpportunities(manager, page, size));
    }

    @PutMapping("/opportunities/{id}/assignment")
    public OpportunityView reassign(@AuthenticationPrincipal CrmPrincipal manager, @PathVariable UUID id,
            @RequestBody @jakarta.validation.Valid AssignOpportunityInput input) {
        return opportunities.reassign(manager, id, input.aeId());
    }

    @GetMapping("/opportunities/{id}/assignment-history")
    public List<AssignmentHistoryView> assignmentHistory(@AuthenticationPrincipal CrmPrincipal manager,
            @PathVariable UUID id) {
        return opportunities.assignmentHistory(manager, id);
    }

    @GetMapping("/opportunity-reopen-requests")
    public List<ReopenRequestView> pendingReopenRequests() { return lifecycle.pendingReopenRequests(); }

    @PostMapping("/opportunity-reopen-requests/{id}/decision")
    public ReopenRequestView decideReopenRequest(@AuthenticationPrincipal CrmPrincipal manager, @PathVariable UUID id,
            @RequestBody @jakarta.validation.Valid ReopenDecisionInput input) { return lifecycle.reviewReopenRequest(manager, id, input); }
}
