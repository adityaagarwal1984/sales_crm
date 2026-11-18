package com.prosperr.crm.prospecting.repository;

import com.prosperr.crm.prospecting.domain.SqlQualificationHistoryEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SqlQualificationHistoryRepository extends JpaRepository<SqlQualificationHistoryEntity,UUID> {
    List<SqlQualificationHistoryEntity> findByQualification_IdOrderByCreatedAtDesc(UUID qualificationId);
}
