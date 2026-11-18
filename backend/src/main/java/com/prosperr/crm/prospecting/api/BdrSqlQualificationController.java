package com.prosperr.crm.prospecting.api;

import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.prospecting.api.ProspectingDtos.*;
import com.prosperr.crm.prospecting.service.SqlQualificationService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bdr/sql-qualifications")
public class BdrSqlQualificationController {
    private final SqlQualificationService service;
    public BdrSqlQualificationController(SqlQualificationService service){this.service=service;}
    @GetMapping public PageResponse<SqlQualificationView> list(@AuthenticationPrincipal CrmPrincipal actor,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return PageResponse.from(service.listBdr(actor,page,size));}
    @PostMapping public SqlQualificationDetail create(@AuthenticationPrincipal CrmPrincipal actor,@Valid @RequestBody SqlQualificationInput input){return service.createDraft(actor,input);}
    @GetMapping("/by-lead/{leadId}") public SqlQualificationDetail getByLead(@AuthenticationPrincipal CrmPrincipal actor,@PathVariable UUID leadId){return service.getBdrByLead(actor,leadId);}
    @GetMapping("/{id}") public SqlQualificationDetail get(@AuthenticationPrincipal CrmPrincipal actor,@PathVariable UUID id){return service.getBdr(actor,id);}
    @PutMapping("/{id}") public SqlQualificationDetail saveDraft(@AuthenticationPrincipal CrmPrincipal actor,@PathVariable UUID id,@Valid @RequestBody SqlQualificationInput input){return service.saveDraft(actor,id,input);}
    @PostMapping("/{id}/submit") public SqlQualificationDetail submit(@AuthenticationPrincipal CrmPrincipal actor,@PathVariable UUID id){return service.submit(actor,id);}
}
