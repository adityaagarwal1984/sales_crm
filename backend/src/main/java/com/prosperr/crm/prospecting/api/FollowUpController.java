package com.prosperr.crm.prospecting.api;

import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.prospecting.api.ProspectingDtos.*;
import com.prosperr.crm.prospecting.service.ProspectingService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bdr/follow-ups")
public class FollowUpController {
    private final ProspectingService service;
    public FollowUpController(ProspectingService service){this.service=service;}
    @GetMapping public PageResponse<FollowUpView> list(@AuthenticationPrincipal CrmPrincipal principal,@RequestParam(defaultValue="UPCOMING") String filter,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return PageResponse.from(service.listFollowUps(principal,filter,page,size));}
    @PatchMapping("/{id}") public FollowUpView reschedule(@AuthenticationPrincipal CrmPrincipal principal,@PathVariable UUID id,@Valid @RequestBody RescheduleInput input){return service.rescheduleFollowUp(principal,id,input.dueAt());}
    @PostMapping("/{id}/complete") public CallResult complete(@AuthenticationPrincipal CrmPrincipal principal,@PathVariable UUID id,@Valid @RequestBody FollowUpCallInput input){return service.completeFollowUp(principal,id,input);}
}
