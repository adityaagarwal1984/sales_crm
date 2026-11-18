package com.prosperr.crm.prospecting.api;

import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.prospecting.api.ProspectingDtos.DashboardView;
import com.prosperr.crm.prospecting.service.ProspectingService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bdr/dashboard")
public class BdrDashboardController {
    private final ProspectingService service;
    public BdrDashboardController(ProspectingService service){this.service=service;}
    @GetMapping public DashboardView dashboard(@AuthenticationPrincipal CrmPrincipal principal){return service.dashboard(principal);}
}
