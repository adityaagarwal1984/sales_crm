package com.prosperr.crm.sales.repository;

import com.prosperr.crm.sales.domain.OpportunityStakeholderEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpportunityStakeholderRepository extends JpaRepository<OpportunityStakeholderEntity, UUID> {
    List<OpportunityStakeholderEntity> findByOpportunity_IdOrderByCreatedAtAsc(UUID opportunityId);
    Optional<OpportunityStakeholderEntity> findByOpportunity_IdAndContact_Id(UUID opportunityId, UUID contactId);
    boolean existsByOpportunity_IdAndContact_Id(UUID opportunityId, UUID contactId);
}
