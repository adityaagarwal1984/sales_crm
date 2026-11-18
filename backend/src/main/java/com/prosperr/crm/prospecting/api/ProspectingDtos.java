package com.prosperr.crm.prospecting.api;

import com.prosperr.crm.prospecting.domain.CallOutcome;
import com.prosperr.crm.prospecting.domain.FollowUpStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;

public final class ProspectingDtos {
    private ProspectingDtos() {}
    public record LeadInput(@NotBlank @Size(max=180) String title,@Size(max=10000) String description,@NotNull UUID organisationId,@NotNull UUID primaryContactId) {}
    public record LeadView(UUID id,String title,String description,UUID organisationId,String organisationName,UUID primaryContactId,String primaryContactName,String primaryContactEmail,UUID ownerId,String ownerName,UUID createdById,Instant createdAt,Instant updatedAt,CallOutcome lastCallOutcome,Instant lastCallAt) {}
    public record CallInput(@NotNull UUID contactId,@NotNull Instant calledAt,@NotNull CallOutcome outcome,@Size(max=10000) String notes,Instant nextFollowUpAt) {}
    public record FollowUpCallInput(@NotNull Instant calledAt,@NotNull CallOutcome outcome,@Size(max=10000) String notes,Instant nextFollowUpAt) {}
    public record RescheduleInput(@NotNull Instant dueAt) {}
    public record CallView(UUID id,UUID leadId,String leadTitle,UUID contactId,String contactName,UUID createdById,String createdByName,Instant calledAt,CallOutcome outcome,String notes,Instant nextFollowUpAt,Instant createdAt) {}
    public record FollowUpView(UUID id,UUID leadId,String leadTitle,UUID contactId,String contactName,Instant dueAt,FollowUpStatus status,UUID sourceCallId,CallOutcome sourceCallOutcome,Instant createdAt,Instant updatedAt,Instant completedAt,UUID completedCallId) {}
    public record SqlSubmissionView(UUID id,UUID leadId,String leadTitle,String leadDescription,UUID organisationId,String organisationName,UUID primaryContactId,String primaryContactName,String primaryContactEmail,UUID submittedById,String submittedByName,Instant submittedAt,String status) {}
    public record DashboardView(long myLeads,long organisationsAdded,long contactsAdded,long callsCompleted,long interestedProspects,long sqlsGenerated,long pendingFollowUps,long overdueFollowUps) {}
    public record CallResult(CallView call,FollowUpView followUp) {}
    public record PageResponse<T>(List<T> content,int number,int size,long totalElements,int totalPages) {
        public static <T> PageResponse<T> from(Page<T> page){return new PageResponse<>(page.getContent(),page.getNumber(),page.getSize(),page.getTotalElements(),page.getTotalPages());}
    }
}
