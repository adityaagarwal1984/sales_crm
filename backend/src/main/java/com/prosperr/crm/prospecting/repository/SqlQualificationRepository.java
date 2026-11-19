package com.prosperr.crm.prospecting.repository;

import com.prosperr.crm.prospecting.domain.SqlQualificationEntity;
import com.prosperr.crm.prospecting.domain.SqlQualificationStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SqlQualificationRepository extends JpaRepository<SqlQualificationEntity,UUID> {
    Optional<SqlQualificationEntity> findByIdAndCreatedBy_Id(UUID id,UUID createdById);
    Optional<SqlQualificationEntity> findByLead_IdAndCreatedBy_Id(UUID leadId,UUID createdById);
    boolean existsByLead_Id(UUID leadId);
    Page<SqlQualificationEntity> findByCreatedBy_IdOrderByUpdatedAtDesc(UUID createdById,Pageable pageable);
    Page<SqlQualificationEntity> findByStatusOrderBySubmittedAtDesc(SqlQualificationStatus status,Pageable pageable);
    Page<SqlQualificationEntity> findAllByOrderByUpdatedAtDesc(Pageable pageable);
    long countByCreatedBy_IdAndStatusNot(UUID createdById,SqlQualificationStatus status);
    long countByStatus(SqlQualificationStatus status);
    @Query("select count(q) from SqlQualificationEntity q where q.status = :status and not exists (select o.id from OpportunityEntity o where o.qualification = q)")
    long countUnassignedByStatus(@Param("status") SqlQualificationStatus status);
    @Query(value = "select q from SqlQualificationEntity q where q.status = :status and not exists (select o.id from OpportunityEntity o where o.qualification = q) order by q.reviewedAt desc",
        countQuery = "select count(q) from SqlQualificationEntity q where q.status = :status and not exists (select o.id from OpportunityEntity o where o.qualification = q)")
    Page<SqlQualificationEntity> findUnassignedByStatus(@Param("status") SqlQualificationStatus status, Pageable pageable);
}
