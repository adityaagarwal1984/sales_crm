package com.prosperr.crm.sales.repository;

import com.prosperr.crm.sales.domain.OpportunityMeetingEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpportunityMeetingRepository extends JpaRepository<OpportunityMeetingEntity, UUID> {
    long countByAeUser_SalesManager_Id(UUID managerId);
    long countByAeUser_Id(UUID aeId);
    long countByAeUser_IdAndMeetingLevel(UUID aeId, com.prosperr.crm.sales.domain.OpportunityMeetingLevel level);
    @org.springframework.data.jpa.repository.Query("select count(m) from OpportunityMeetingEntity m where m.aeUser.id = :aeId and m.followUpAt is not null and m.followUpCompletedAt is null")
    long countPendingFollowUps(@org.springframework.data.repository.query.Param("aeId") UUID aeId);
    List<OpportunityMeetingEntity> findByAeUser_IdOrderByCompletedAtDesc(UUID aeId);
    List<OpportunityMeetingEntity> findByOpportunity_IdOrderByCompletedAtDesc(UUID opportunityId);
    java.util.Optional<OpportunityMeetingEntity> findByIdAndAeUser_Id(UUID id, UUID aeId);
}
