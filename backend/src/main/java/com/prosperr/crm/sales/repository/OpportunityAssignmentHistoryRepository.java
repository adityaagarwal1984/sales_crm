package com.prosperr.crm.sales.repository;

import com.prosperr.crm.sales.domain.OpportunityAssignmentHistoryEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpportunityAssignmentHistoryRepository extends JpaRepository<OpportunityAssignmentHistoryEntity, UUID> {
    List<OpportunityAssignmentHistoryEntity> findByOpportunity_IdOrderByCreatedAtDesc(UUID opportunityId);
}
