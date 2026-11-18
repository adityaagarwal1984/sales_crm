package com.prosperr.crm.prospecting.repository;

import com.prosperr.crm.prospecting.domain.FollowUpEntity;
import com.prosperr.crm.prospecting.domain.FollowUpStatus;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowUpRepository extends JpaRepository<FollowUpEntity,UUID> {
    Optional<FollowUpEntity> findByIdAndOwner_Id(UUID id,UUID ownerId);
    Page<FollowUpEntity> findByOwner_IdAndStatusAndDueAtGreaterThanEqualOrderByDueAtAsc(UUID ownerId,FollowUpStatus status,Instant now,Pageable pageable);
    Page<FollowUpEntity> findByOwner_IdAndStatusAndDueAtBeforeOrderByDueAtAsc(UUID ownerId,FollowUpStatus status,Instant now,Pageable pageable);
    Page<FollowUpEntity> findByOwner_IdAndStatusOrderByDueAtDesc(UUID ownerId,FollowUpStatus status,Pageable pageable);
    Page<FollowUpEntity> findByOwner_IdOrderByDueAtDesc(UUID ownerId,Pageable pageable);
    long countByOwner_IdAndStatusAndDueAtGreaterThanEqual(UUID ownerId,FollowUpStatus status,Instant now);
    long countByOwner_IdAndStatusAndDueAtBefore(UUID ownerId,FollowUpStatus status,Instant now);
    long countByOwner_IdAndStatus(UUID ownerId,FollowUpStatus status);
}
