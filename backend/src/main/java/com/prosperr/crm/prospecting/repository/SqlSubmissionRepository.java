package com.prosperr.crm.prospecting.repository;

import com.prosperr.crm.prospecting.domain.SqlSubmissionEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlSubmissionRepository extends JpaRepository<SqlSubmissionEntity,UUID> {
    Page<SqlSubmissionEntity> findBySubmittedBy_IdOrderBySubmittedAtDesc(UUID userId,Pageable pageable);
    Page<SqlSubmissionEntity> findAllByOrderBySubmittedAtDesc(Pageable pageable);
    long countBySubmittedBy_Id(UUID userId);
}
