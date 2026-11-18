package com.prosperr.crm.identity.security;

import com.prosperr.crm.identity.domain.RoleCode;
import java.util.UUID;

public record CrmPrincipal(UUID id, String email, String fullName, RoleCode role, boolean mustChangePassword) {
}
