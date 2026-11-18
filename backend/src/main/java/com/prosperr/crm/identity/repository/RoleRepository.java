package com.prosperr.crm.identity.repository;

import com.prosperr.crm.identity.domain.RoleCode;
import com.prosperr.crm.identity.domain.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, RoleCode> {
}
