package com.prosperr.crm.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_role")
public class RoleEntity {

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "code", nullable = false, length = 32)
    private RoleCode code;

    @Column(name = "display_name", nullable = false, length = 80)
    private String displayName;

    protected RoleEntity() {
    }

    public RoleEntity(RoleCode code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public RoleCode getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }
}
