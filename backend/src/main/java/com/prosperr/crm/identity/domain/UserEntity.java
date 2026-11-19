package com.prosperr.crm.identity.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "app_user", uniqueConstraints = @UniqueConstraint(name = "uq_app_user_email", columnNames = "email"))
public class UserEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(name = "email", nullable = false, length = 320)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_code", nullable = false)
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private UserEntity salesManager;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "must_change_password", nullable = false)
    private boolean mustChangePassword;

    @Column(name = "credentials_changed_at", nullable = false)
    private Instant credentialsChangedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserEntity() {
    }

    public UserEntity(String fullName, String email, String passwordHash, RoleEntity role, boolean mustChangePassword) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = true;
        this.mustChangePassword = mustChangePassword;
    }

    @PrePersist
    void beforeInsert() {
        Instant now = Instant.now();
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = now;
        if (credentialsChangedAt == null) credentialsChangedAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void beforeUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public RoleEntity getRole() { return role; }
    public UserEntity getSalesManager() { return salesManager; }
    public boolean isActive() { return active; }
    public boolean isMustChangePassword() { return mustChangePassword; }
    public Instant getCredentialsChangedAt() { return credentialsChangedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void updateProfile(String fullName, String email, RoleEntity role, UserEntity salesManager) {
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.salesManager = salesManager;
    }

    public void assignToManager(UserEntity salesManager) { this.salesManager = salesManager; }

    public void setActive(boolean active) { this.active = active; }

    public void setTemporaryPassword(String passwordHash, Instant changedAt) {
        this.passwordHash = passwordHash;
        this.mustChangePassword = true;
        this.credentialsChangedAt = changedAt;
    }

    public void changePassword(String passwordHash, Instant changedAt) {
        this.passwordHash = passwordHash;
        this.mustChangePassword = false;
        this.credentialsChangedAt = changedAt;
    }
}
