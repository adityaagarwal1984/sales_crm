package com.prosperr.crm.sales.repository;

import com.prosperr.crm.sales.domain.AppNotificationEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppNotificationRepository extends JpaRepository<AppNotificationEntity, UUID> {
    List<AppNotificationEntity> findTop30ByRecipient_IdOrderByCreatedAtDesc(UUID recipientId);
    Optional<AppNotificationEntity> findByIdAndRecipient_Id(UUID id, UUID recipientId);
    long countByRecipient_IdAndReadAtIsNull(UUID recipientId);
}
