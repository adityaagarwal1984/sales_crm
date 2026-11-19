package com.prosperr.crm.identity.repository;

import com.prosperr.crm.identity.domain.RoleCode;
import com.prosperr.crm.identity.domain.UserEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByRole_Code(RoleCode roleCode);
    long countByRole_Code(RoleCode roleCode);
    long countByRole_CodeAndActiveTrue(RoleCode roleCode);
    List<UserEntity> findAllByRole_CodeOrderByFullNameAsc(RoleCode roleCode);
    List<UserEntity> findAllByRole_CodeAndActiveTrueOrderByFullNameAsc(RoleCode roleCode);
    List<UserEntity> findAllBySalesManager_IdAndRole_CodeOrderByFullNameAsc(UUID managerId, RoleCode roleCode);
    long countBySalesManager_Id(UUID managerId);
    long countBySalesManager_IdAndRole_Code(UUID managerId, RoleCode roleCode);
}
