package com.prosperr.crm.organisation.repository;

import java.util.UUID;

public interface OrganisationContactCount {
    UUID getOrganisationId();
    long getContactCount();
}
