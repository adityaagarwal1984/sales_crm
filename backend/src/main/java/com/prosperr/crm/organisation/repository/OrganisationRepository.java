package com.prosperr.crm.organisation.repository;

import com.prosperr.crm.organisation.domain.OrganisationEntity;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganisationRepository extends JpaRepository<OrganisationEntity, UUID> {
    long countByCreatedBy_Id(UUID userId);
    @Query("select o from OrganisationEntity o where (lower(o.name) like :searchPattern or lower(coalesce(o.website,'')) like :searchPattern or lower(coalesce(o.industry,'')) like :searchPattern or lower(coalesce(o.location,'')) like :searchPattern) and (:industry='' or lower(o.industry)=:industry) and (:companySize='' or lower(o.companySize)=:companySize) and (:leadSource='' or lower(o.leadSource)=:leadSource)")
    Page<OrganisationEntity> search(@Param("searchPattern") String searchPattern,@Param("industry") String industry,@Param("companySize") String companySize,@Param("leadSource") String leadSource,Pageable pageable);
    @Query("select (count(o)>0) from OrganisationEntity o where lower(trim(o.name))=:normalizedName")
    boolean existsByNormalizedName(@Param("normalizedName") String normalizedName);
    @Query("select (count(o)>0) from OrganisationEntity o where lower(trim(o.name))=:normalizedName and o.id<>:id")
    boolean existsByNormalizedNameAndIdNot(@Param("normalizedName") String normalizedName,@Param("id") UUID id);
}
