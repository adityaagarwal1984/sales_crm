package com.prosperr.crm.sales.api;

import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.sales.api.SalesDtos.NotificationList;
import com.prosperr.crm.sales.api.SalesDtos.NotificationView;
import com.prosperr.crm.sales.service.AppNotificationService;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    private final AppNotificationService notifications;

    public NotificationController(AppNotificationService notifications) { this.notifications = notifications; }

    @GetMapping
    public NotificationList list(@AuthenticationPrincipal CrmPrincipal actor) { return notifications.list(actor); }

    @PostMapping("/{id}/read")
    public NotificationView markRead(@AuthenticationPrincipal CrmPrincipal actor, @PathVariable UUID id) {
        return notifications.markRead(actor, id);
    }
}
