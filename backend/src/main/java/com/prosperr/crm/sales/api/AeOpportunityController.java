package com.prosperr.crm.sales.api;

import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.prospecting.api.ProspectingDtos.PageResponse;
import com.prosperr.crm.sales.api.SalesDtos.*;
import com.prosperr.crm.sales.domain.OpportunityStatus;
import com.prosperr.crm.sales.service.OpportunityService;
import com.prosperr.crm.sales.service.OpportunityLifecycleService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ae/opportunities")
public class AeOpportunityController {
    private final OpportunityService opportunities;
    private final OpportunityLifecycleService lifecycle;

    public AeOpportunityController(OpportunityService opportunities, OpportunityLifecycleService lifecycle) { this.opportunities = opportunities; this.lifecycle = lifecycle; }

    @GetMapping("/dashboard")
    public AeDashboardView dashboard(@AuthenticationPrincipal CrmPrincipal ae) { return lifecycle.dashboard(ae); }

    @GetMapping
    public PageResponse<OpportunityView> list(@AuthenticationPrincipal CrmPrincipal ae,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return PageResponse.from(opportunities.aeOpportunities(ae, page, size));
    }

    @GetMapping("/{id}")
    public OpportunityDetailView detail(@AuthenticationPrincipal CrmPrincipal ae, @PathVariable UUID id) { return lifecycle.detail(ae, id); }

    @PostMapping("/{id}/meetings")
    public MeetingView logMeeting(@AuthenticationPrincipal CrmPrincipal ae, @PathVariable UUID id,
            @Valid @RequestBody CompletedMeetingInput input) {
        return lifecycle.logMeeting(ae, id, input);
    }

    @PatchMapping("/{id}/stage")
    public OpportunityView transition(@AuthenticationPrincipal CrmPrincipal ae, @PathVariable UUID id, @Valid @RequestBody StageTransitionInput input) { return lifecycle.transition(ae, id, input); }

    @PatchMapping("/{id}/close-won")
    public OpportunityDetailView closeWon(@AuthenticationPrincipal CrmPrincipal ae, @PathVariable UUID id, @Valid @RequestBody CloseWonInput input) { return lifecycle.closeWon(ae, id, input); }

    @PatchMapping("/{id}/close-lost")
    public OpportunityDetailView closeLost(@AuthenticationPrincipal CrmPrincipal ae, @PathVariable UUID id, @Valid @RequestBody CloseLostInput input) { return lifecycle.closeLost(ae, id, input); }

    @PostMapping("/{id}/stakeholders")
    public StakeholderView addStakeholder(@AuthenticationPrincipal CrmPrincipal ae, @PathVariable UUID id, @Valid @RequestBody StakeholderInput input) { return lifecycle.addStakeholder(ae, id, input); }

    @DeleteMapping("/{id}/stakeholders/{contactId}")
    public void removeStakeholder(@AuthenticationPrincipal CrmPrincipal ae, @PathVariable UUID id, @PathVariable UUID contactId) { lifecycle.removeStakeholder(ae, id, contactId); }

    @PostMapping("/{id}/meetings/{meetingId}/follow-up/complete")
    public MeetingView completeFollowUp(@AuthenticationPrincipal CrmPrincipal ae, @PathVariable UUID id, @PathVariable UUID meetingId) { return lifecycle.completeFollowUp(ae, id, meetingId); }

    @PostMapping("/{id}/reopen-request")
    public ReopenRequestView requestReopen(@AuthenticationPrincipal CrmPrincipal ae, @PathVariable UUID id, @Valid @RequestBody ReopenRequestInput input) { return lifecycle.requestReopen(ae, id, input); }
}
