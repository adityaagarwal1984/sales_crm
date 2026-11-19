package com.prosperr.crm.analytics.api;

import com.prosperr.crm.analytics.service.LeadershipAnalyticsService;
import com.prosperr.crm.sales.api.SalesDtos.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/leadership/analytics")
public class LeadershipAnalyticsController {
    private final LeadershipAnalyticsService analytics;
    public LeadershipAnalyticsController(LeadershipAnalyticsService analytics) { this.analytics = analytics; }

    @GetMapping("/filters") public LeadershipFilterOptions filters() { return analytics.filterOptions(); }
    @GetMapping("/overview") public LeadershipOverviewView overview(@ModelAttribute AnalyticsFilter filter) { return analytics.overview(filter); }
    @GetMapping("/bdrs") public java.util.List<BdrAnalyticsView> bdrs(@ModelAttribute AnalyticsFilter filter) { return analytics.bdrs(filter); }
    @GetMapping("/sales-managers") public java.util.List<ManagerAnalyticsView> managers(@ModelAttribute AnalyticsFilter filter) { return analytics.managers(filter); }
    @GetMapping("/aes") public java.util.List<AeAnalyticsView> aes(@ModelAttribute AnalyticsFilter filter) { return analytics.aes(filter); }
    @GetMapping("/funnel") public java.util.List<FunnelStepView> funnel(@ModelAttribute AnalyticsFilter filter) { return analytics.funnel(filter); }
    @GetMapping("/revenue") public LeadershipRevenueView revenue(@ModelAttribute AnalyticsFilter filter) { return analytics.revenue(filter); }
}
