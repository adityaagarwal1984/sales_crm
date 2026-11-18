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
@RequestMapping("/api/v1/bdr/leads")
public class LeadController {
    private final ProspectingService service;
    public LeadController(ProspectingService service){this.service=service;}
    @GetMapping public PageResponse<LeadView> list(@AuthenticationPrincipal CrmPrincipal principal,@RequestParam(required=false) String search,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return PageResponse.from(service.listLeads(principal,search,page,size));}
    @PostMapping public LeadView create(@AuthenticationPrincipal CrmPrincipal principal,@Valid @RequestBody LeadInput input){return service.createLead(principal,input);}
    @GetMapping("/{id}") public LeadView get(@AuthenticationPrincipal CrmPrincipal principal,@PathVariable UUID id){return service.getLead(principal,id);}
    @PutMapping("/{id}") public LeadView update(@AuthenticationPrincipal CrmPrincipal principal,@PathVariable UUID id,@Valid @RequestBody LeadInput input){return service.updateLead(principal,id,input);}
}
