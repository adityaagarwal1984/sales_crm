package com.prosperr.crm.prospecting.api;

import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.prospecting.api.ProspectingDtos.*;
import com.prosperr.crm.prospecting.service.ProspectingService;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bdr/sql-submissions")
public class SqlSubmissionController {
    private final ProspectingService service;
    public SqlSubmissionController(ProspectingService service){this.service=service;}
    @GetMapping public PageResponse<SqlSubmissionView> list(@AuthenticationPrincipal CrmPrincipal principal,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return PageResponse.from(service.sqlSubmissions(principal,page,size));}
    @PostMapping("/leads/{leadId}") public SqlSubmissionView create(@AuthenticationPrincipal CrmPrincipal principal,@PathVariable UUID leadId){return service.createSqlSubmission(principal,leadId);}
}
