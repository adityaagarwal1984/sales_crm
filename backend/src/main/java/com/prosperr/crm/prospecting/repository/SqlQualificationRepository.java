package com.prosperr.crm.prospecting.repository;

import com.prosperr.crm.prospecting.domain.SqlQualificationEntity;
import com.prosperr.crm.prospecting.domain.SqlQualificationStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlQualificationRepository extends JpaRepository<SqlQualificationEntity,UUID> {
    Optional<SqlQualificationEntity> findByIdAndCreatedBy_Id(UUID id,UUID createdById);
    Optional<SqlQualificationEntity> findByLead_IdAndCreatedBy_Id(UUID leadId,UUID createdById);
    boolean existsByLead_Id(UUID leadId);
    Page<SqlQualificationEntity> findByCreatedBy_IdOrderByUpdatedAtDesc(UUID createdById,Pageable pageable);
    Page<SqlQualificationEntity> findByStatusOrderBySubmittedAtDesc(SqlQualificationStatus status,Pageable pageable);
    Page<SqlQualificationEntity> findAllByOrderByUpdatedAtDesc(Pageable pageable);
    long countByCreatedBy_IdAndStatusNot(UUID createdById,SqlQualificationStatus status);
}
