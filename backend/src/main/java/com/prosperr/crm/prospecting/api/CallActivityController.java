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
@RequestMapping("/api/v1/bdr")
public class CallActivityController {
    private final ProspectingService service;
    public CallActivityController(ProspectingService service){this.service=service;}
    @GetMapping("/calls") public PageResponse<CallView> all(@AuthenticationPrincipal CrmPrincipal principal,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return PageResponse.from(service.allCalls(principal,page,size));}
    @GetMapping("/leads/{leadId}/calls") public PageResponse<CallView> history(@AuthenticationPrincipal CrmPrincipal principal,@PathVariable UUID leadId,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return PageResponse.from(service.callsForLead(principal,leadId,page,size));}
    @PostMapping("/leads/{leadId}/calls") public CallResult record(@AuthenticationPrincipal CrmPrincipal principal,@PathVariable UUID leadId,@Valid @RequestBody CallInput input){return service.recordCall(principal,leadId,input);}
}
