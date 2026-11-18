package com.prosperr.crm.organisation.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.domain.Page;

public final class OrganisationDtos {
    private OrganisationDtos() {}
    public record OrganisationInput(@NotBlank @Size(max=180) String name,@Size(max=500) @URL String website,@Size(max=120) String industry,@Size(max=80) String companySize,@Size(max=180) String location,@Size(max=10000) String address,@Size(max=120) String leadSource,@Size(max=10000) String description) {}
    public record OrganisationSummary(UUID id,String name,String website,String industry,String companySize,String location,String leadSource,long contactCount,Instant createdAt) {}
    public record OrganisationView(UUID id,String name,String website,String industry,String companySize,String location,String address,String leadSource,String description,UUID createdById,String createdByName,Instant createdAt,Instant updatedAt,List<ContactView> contacts) {}
    public record ContactInput(@NotBlank @Size(max=180) String name,@Email @Size(max=320) String email,@Size(max=40) String phone,@Size(max=120) String designation,@Size(max=120) String department,@Size(max=500) @URL String linkedinUrl,UUID organisationId) {}
    public record ContactView(UUID id,String name,String email,String phone,String designation,String department,String linkedinUrl,Instant createdAt,Instant updatedAt,UUID createdById,String createdByName,OrganisationSummary organisation) {}
    public record PageResponse<T>(List<T> content,int number,int size,long totalElements,int totalPages) {
        public static <T> PageResponse<T> from(Page<T> page){return new PageResponse<>(page.getContent(),page.getNumber(),page.getSize(),page.getTotalElements(),page.getTotalPages());}
    }
}
