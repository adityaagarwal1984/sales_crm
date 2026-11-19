package com.prosperr.crm.sales.repository;

import com.prosperr.crm.sales.domain.OpportunityReopenRequestEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpportunityReopenRequestRepository extends JpaRepository<OpportunityReopenRequestEntity, UUID> {
    boolean existsByOpportunity_IdAndStatus(UUID opportunityId, String status);
    List<OpportunityReopenRequestEntity> findByStatusOrderByCreatedAtAsc(String status);
    Optional<OpportunityReopenRequestEntity> findByIdAndStatus(UUID id, String status);
}
