package com.prosperr.crm.sales.repository;

import com.prosperr.crm.sales.domain.OpportunityStageHistoryEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpportunityStageHistoryRepository extends JpaRepository<OpportunityStageHistoryEntity, UUID> {
    List<OpportunityStageHistoryEntity> findByOpportunity_IdOrderByCreatedAtAsc(UUID opportunityId);
}
