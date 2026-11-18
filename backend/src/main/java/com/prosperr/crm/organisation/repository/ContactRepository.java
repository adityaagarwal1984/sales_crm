package com.prosperr.crm.organisation.repository;

import com.prosperr.crm.organisation.domain.ContactEntity;
import java.util.List;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<ContactEntity, UUID> {
    @Query("select c from ContactEntity c join c.organisation o where (lower(c.name) like :searchPattern or lower(coalesce(c.email,'')) like :searchPattern or lower(coalesce(c.phone,'')) like :searchPattern or lower(coalesce(c.designation,'')) like :searchPattern or lower(coalesce(c.department,'')) like :searchPattern or lower(o.name) like :searchPattern) and (:organisationId is null or o.id=:organisationId)")
    Page<ContactEntity> search(@Param("searchPattern") String searchPattern,@Param("organisationId") UUID organisationId,Pageable pageable);
    List<ContactEntity> findAllByOrganisation_IdOrderByNameAsc(UUID organisationId);
    @Query("select (count(c)>0) from ContactEntity c where c.organisation.id=:organisationId and c.email is not null and lower(trim(c.email))=:normalizedEmail")
    boolean existsByOrganisationAndNormalizedEmail(@Param("organisationId") UUID organisationId,@Param("normalizedEmail") String normalizedEmail);
    @Query("select (count(c)>0) from ContactEntity c where c.organisation.id=:organisationId and c.email is not null and lower(trim(c.email))=:normalizedEmail and c.id<>:id")
    boolean existsByOrganisationAndNormalizedEmailAndIdNot(@Param("organisationId") UUID organisationId,@Param("normalizedEmail") String normalizedEmail,@Param("id") UUID id);
    long countByOrganisation_Id(UUID organisationId);
    long countByCreatedBy_Id(UUID userId);
    @Query("select o.id as organisationId, count(c.id) as contactCount from OrganisationEntity o left join ContactEntity c on c.organisation=o where o.id in :ids group by o.id")
    List<OrganisationContactCount> countByOrganisationIds(@Param("ids") Collection<UUID> organisationIds);
}
