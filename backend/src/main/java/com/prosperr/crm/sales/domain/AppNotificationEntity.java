package com.prosperr.crm.sales.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_notification")
public class AppNotificationEntity {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false, updatable = false)
    private UserEntity recipient;

    @Column(nullable = false, length = 180, updatable = false)
    private String title;

    @Column(nullable = false, length = 1000, updatable = false)
    private String message;

    @Column(name = "target_path", nullable = false, length = 500, updatable = false)
    private String targetPath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "read_at")
    private Instant readAt;

    protected AppNotificationEntity() {}

    public AppNotificationEntity(UserEntity recipient, String title, String message, String targetPath) {
        this.recipient = recipient;
        this.title = title;
        this.message = message;
        this.targetPath = targetPath;
    }

    @PrePersist
    void beforeInsert() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = Instant.now();
    }

    public void markRead(Instant at) { if (readAt == null) readAt = at; }
    public UUID getId() { return id; }
    public UserEntity getRecipient() { return recipient; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTargetPath() { return targetPath; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getReadAt() { return readAt; }
}
