package com.prosperr.crm.prospecting.api;

import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.prospecting.api.ProspectingDtos.*;
import com.prosperr.crm.prospecting.service.SqlQualificationService;
import com.prosperr.crm.sales.api.SalesDtos.AssignOpportunityInput;
import com.prosperr.crm.sales.api.SalesDtos.OpportunityView;
import com.prosperr.crm.sales.service.OpportunityService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sales-manager/sql-qualifications")
public class SalesManagerSqlQualificationController {
    private final SqlQualificationService service;
    private final OpportunityService opportunities;
    public SalesManagerSqlQualificationController(SqlQualificationService service,OpportunityService opportunities){this.service=service;this.opportunities=opportunities;}
    @GetMapping public PageResponse<SqlQualificationView> queue(@AuthenticationPrincipal CrmPrincipal actor,@RequestParam(required=false) String status,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return PageResponse.from(service.managerQueue(actor,status,page,size));}
    @GetMapping("/{id}") public SqlQualificationDetail get(@AuthenticationPrincipal CrmPrincipal actor,@PathVariable UUID id){return service.getForManager(actor,id);}
    @PostMapping("/{id}/approve") public SqlQualificationDetail approve(@AuthenticationPrincipal CrmPrincipal actor,@PathVariable UUID id){return service.approve(actor,id);}
    @PostMapping("/{id}/reject") public SqlQualificationDetail reject(@AuthenticationPrincipal CrmPrincipal actor,@PathVariable UUID id,@Valid @RequestBody SqlRejectionInput input){return service.reject(actor,id,input.reason());}
    @GetMapping("/approved-for-assignment") public PageResponse<SqlQualificationView> approvedForAssignment(@AuthenticationPrincipal CrmPrincipal actor,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){return PageResponse.from(service.unassignedApprovedForManager(actor,page,size));}
    @PostMapping("/{id}/assign") public OpportunityView assign(@AuthenticationPrincipal CrmPrincipal actor,@PathVariable UUID id,@Valid @RequestBody AssignOpportunityInput input){return opportunities.assignApprovedSql(actor,id,input.aeId());}
}
