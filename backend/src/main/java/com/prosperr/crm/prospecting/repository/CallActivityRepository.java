package com.prosperr.crm.prospecting.repository;

import com.prosperr.crm.prospecting.domain.CallActivityEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallActivityRepository extends JpaRepository<CallActivityEntity,UUID> {
    Page<CallActivityEntity> findByLead_IdAndLead_Owner_IdOrderByCalledAtDesc(UUID leadId,UUID ownerId,Pageable pageable);
    Page<CallActivityEntity> findByLead_Owner_IdOrderByCalledAtDesc(UUID ownerId,Pageable pageable);
    long countByCreatedBy_Id(UUID userId);
}
