package com.prosperr.crm.sales.service;

import com.prosperr.crm.identity.security.CrmPrincipal;
import com.prosperr.crm.sales.api.SalesDtos.NotificationList;
import com.prosperr.crm.sales.api.SalesDtos.NotificationView;
import com.prosperr.crm.sales.domain.AppNotificationEntity;
import com.prosperr.crm.sales.repository.AppNotificationRepository;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AppNotificationService {
    private final AppNotificationRepository notifications;

    public AppNotificationService(AppNotificationRepository notifications) {
        this.notifications = notifications;
    }

    @Transactional
    public void notifyOpportunityAssigned(com.prosperr.crm.identity.domain.UserEntity recipient,
            String title, String message) {
        notifications.save(new AppNotificationEntity(recipient, title, message, "/ae/opportunities"));
    }

    @Transactional(readOnly = true)
    public NotificationList list(CrmPrincipal actor) {
        var items = notifications.findTop30ByRecipient_IdOrderByCreatedAtDesc(actor.id()).stream()
                .map(this::view).toList();
        return new NotificationList(items, notifications.countByRecipient_IdAndReadAtIsNull(actor.id()));
    }

    @Transactional
    public NotificationView markRead(CrmPrincipal actor, UUID id) {
        AppNotificationEntity notification = notifications.findByIdAndRecipient_Id(id, actor.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        notification.markRead(Instant.now());
        return view(notification);
    }

    private NotificationView view(AppNotificationEntity notification) {
        return new NotificationView(notification.getId(), notification.getTitle(), notification.getMessage(),
                notification.getTargetPath(), notification.getCreatedAt(), notification.getReadAt());
    }
}
