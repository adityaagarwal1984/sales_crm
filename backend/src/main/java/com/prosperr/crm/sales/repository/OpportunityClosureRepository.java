package com.prosperr.crm.sales.repository;

import com.prosperr.crm.sales.domain.OpportunityClosureEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpportunityClosureRepository extends JpaRepository<OpportunityClosureEntity, UUID> {
    List<OpportunityClosureEntity> findByOpportunity_IdOrderByClosedAtDesc(UUID opportunityId);
}
