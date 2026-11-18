package com.prosperr.crm.prospecting.repository;

import com.prosperr.crm.prospecting.domain.CallOutcome;
import com.prosperr.crm.prospecting.domain.LeadEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LeadRepository extends JpaRepository<LeadEntity,UUID> {
    Page<LeadEntity> findByOwner_IdOrderByCreatedAtDesc(UUID ownerId,Pageable pageable);
    Optional<LeadEntity> findByIdAndOwner_Id(UUID id,UUID ownerId);
    @Query("select l from LeadEntity l join l.organisation o join l.primaryContact c where l.owner.id=:ownerId and (lower(l.title) like :pattern or lower(o.name) like :pattern or lower(c.name) like :pattern)")
    Page<LeadEntity> searchByOwner(@Param("ownerId") UUID ownerId,@Param("pattern") String pattern,Pageable pageable);
    long countByOwner_Id(UUID ownerId);
    long countByOwner_IdAndLastCallOutcome(UUID ownerId,CallOutcome outcome);
}
