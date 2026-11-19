package com.prosperr.crm.sales.repository;

import com.prosperr.crm.sales.domain.ImplementationHandoverEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImplementationHandoverRepository extends JpaRepository<ImplementationHandoverEntity, UUID> {
    List<ImplementationHandoverEntity> findByOpportunity_IdOrderByCreatedAtDesc(UUID opportunityId);
}
