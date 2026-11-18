package com.prosperr.crm.prospecting.api;

import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.prospecting.api.ProspectingDtos.*;
import com.prosperr.crm.prospecting.service.SqlQualificationService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sales-manager/sql-qualifications")
public class SalesManagerSqlQualificationController {
    private final SqlQualificationService service;
    public SalesManagerSqlQualificationController(SqlQualificationService service){this.service=service;}
    @GetMapping public PageResponse<SqlQualificationView> queue(@RequestParam(required=false) String status,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return PageResponse.from(service.managerQueue(status,page,size));}
    @GetMapping("/{id}") public SqlQualificationDetail get(@PathVariable UUID id){return service.getForManager(id);}
    @PostMapping("/{id}/approve") public SqlQualificationDetail approve(@AuthenticationPrincipal CrmPrincipal actor,@PathVariable UUID id){return service.approve(actor,id);}
    @PostMapping("/{id}/reject") public SqlQualificationDetail reject(@AuthenticationPrincipal CrmPrincipal actor,@PathVariable UUID id,@Valid @RequestBody SqlRejectionInput input){return service.reject(actor,id,input.reason());}
}
