package com.prosperr.crm.sales.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import com.prosperr.crm.organisation.domain.ContactEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "opportunity_meeting")
public class OpportunityMeetingEntity {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "opportunity_id", nullable = false, updatable = false)
    private OpportunityEntity opportunity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ae_user_id", nullable = false, updatable = false)
    private UserEntity aeUser;

    @Column(nullable = false, length = 180, updatable = false)
    private String title;

    @Column(length = 10000, updatable = false)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type", nullable = false, length = 32, updatable = false)
    private OpportunityMeetingType meetingType;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_level", length = 8, updatable = false)
    private OpportunityMeetingLevel meetingLevel;

    @Column(name = "meeting_outcome", nullable = false, length = 5000, updatable = false)
    private String outcome;

    @Column(name = "next_action", length = 5000, updatable = false)
    private String nextAction;

    @Column(name = "follow_up_at", updatable = false)
    private Instant followUpAt;

    @Column(name = "follow_up_completed_at")
    private Instant followUpCompletedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "opportunity_meeting_participant",
        joinColumns = @JoinColumn(name = "meeting_id"),
        inverseJoinColumns = @JoinColumn(name = "contact_id"))
    private List<ContactEntity> participants = new ArrayList<>();

    @Column(name = "completed_at", nullable = false, updatable = false)
    private Instant completedAt;

    protected OpportunityMeetingEntity() {}

    public OpportunityMeetingEntity(OpportunityEntity opportunity, UserEntity aeUser, String title,
            String notes, Instant completedAt) {
        this(opportunity, aeUser, title, notes, completedAt, OpportunityMeetingType.OTHER, null,
                "Completed", null, null, List.of());
    }

    public OpportunityMeetingEntity(OpportunityEntity opportunity, UserEntity aeUser, String title,
            String notes, Instant completedAt, OpportunityMeetingType meetingType,
            OpportunityMeetingLevel meetingLevel, String outcome, String nextAction,
            Instant followUpAt, List<ContactEntity> participants) {
        this.opportunity = opportunity;
        this.aeUser = aeUser;
        this.title = title;
        this.notes = notes;
        this.completedAt = completedAt;
        this.meetingType = meetingType == null ? OpportunityMeetingType.OTHER : meetingType;
        this.meetingLevel = meetingLevel;
        this.outcome = outcome == null || outcome.isBlank() ? "Completed" : outcome.trim();
        this.nextAction = nextAction;
        this.followUpAt = followUpAt;
        this.participants = new ArrayList<>(participants == null ? List.of() : participants);
    }

    public void completeFollowUp(Instant at) { this.followUpCompletedAt = at; }

    @PrePersist
    void beforeInsert() { if (id == null) id = UUID.randomUUID(); }

    public UUID getId() { return id; }
    public OpportunityEntity getOpportunity() { return opportunity; }
    public UserEntity getAeUser() { return aeUser; }
    public String getTitle() { return title; }
    public String getNotes() { return notes; }
    public Instant getCompletedAt() { return completedAt; }
    public OpportunityMeetingType getMeetingType() { return meetingType; }
    public OpportunityMeetingLevel getMeetingLevel() { return meetingLevel; }
    public String getOutcome() { return outcome; }
    public String getNextAction() { return nextAction; }
    public Instant getFollowUpAt() { return followUpAt; }
    public Instant getFollowUpCompletedAt() { return followUpCompletedAt; }
    public List<ContactEntity> getParticipants() { return participants; }
}
