package com.prosperr.crm.sales.repository;

import com.prosperr.crm.sales.domain.OpportunityEntity;
import com.prosperr.crm.sales.domain.OpportunityStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OpportunityRepository extends JpaRepository<OpportunityEntity, UUID> {
    Optional<OpportunityEntity> findByQualification_Id(UUID qualificationId);
    Optional<OpportunityEntity> findByIdAndAssignedAe_Id(UUID id, UUID aeId);
    Page<OpportunityEntity> findAllByOrderByAssignedAtDesc(Pageable pageable);
    Page<OpportunityEntity> findByAssignedAe_IdOrderByAssignedAtDesc(UUID aeId, Pageable pageable);
    List<OpportunityEntity> findAllByAssignedAe_IdOrderByAssignedAtDesc(UUID aeId);
    long count();
    long countByStatus(OpportunityStatus status);
    long countByAssignedAe_Id(UUID aeId);
    long countByAssignedAe_IdAndStatus(UUID aeId, OpportunityStatus status);
    @org.springframework.data.jpa.repository.Query("select coalesce(sum(o.amount), 0) from OpportunityEntity o where o.assignedAe.id = :aeId and o.status in :stages")
    BigDecimal sumPipelineByAe(@org.springframework.data.repository.query.Param("aeId") UUID aeId, @org.springframework.data.repository.query.Param("stages") java.util.Collection<OpportunityStatus> stages);
    @org.springframework.data.jpa.repository.Query("select coalesce(sum(o.finalDealValue), 0) from OpportunityEntity o where o.assignedAe.id = :aeId and o.status = :status")
    BigDecimal sumFinalRevenueByAe(@org.springframework.data.repository.query.Param("aeId") UUID aeId, @org.springframework.data.repository.query.Param("status") OpportunityStatus status);
    @Query("select coalesce(sum(o.amount), 0) from OpportunityEntity o where o.status = :status")
    BigDecimal sumAmountByStatus(@Param("status") OpportunityStatus status);
    @Query("select coalesce(sum(o.amount), 0) from OpportunityEntity o where o.assignedAe.id = :aeId and o.status = :status")
    BigDecimal sumAmountByAeAndStatus(@Param("aeId") UUID aeId, @Param("status") OpportunityStatus status);
}
