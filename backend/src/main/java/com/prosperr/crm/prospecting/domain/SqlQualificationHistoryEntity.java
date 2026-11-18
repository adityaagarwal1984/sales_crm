package com.prosperr.crm.prospecting.domain;

import com.prosperr.crm.identity.domain.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name="sql_qualification_history")
public class SqlQualificationHistoryEntity {
    @Id @Column(nullable=false,updatable=false) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="qualification_id",nullable=false,updatable=false) private SqlQualificationEntity qualification;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="actor_id",nullable=false,updatable=false) private UserEntity actor;
    @Column(nullable=false,length=32,updatable=false) private String action;
    @Column(name="from_status",length=20,updatable=false) private String fromStatus;
    @Column(name="to_status",nullable=false,length=20,updatable=false) private String toStatus;
    @Column(length=5000,updatable=false) private String reason;
    @Column(name="snapshot_json",nullable=false,length=50000,updatable=false) private String snapshotJson;
    @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt;
    protected SqlQualificationHistoryEntity() {}
    public SqlQualificationHistoryEntity(SqlQualificationEntity qualification,UserEntity actor,String action,String fromStatus,String toStatus,String reason,String snapshotJson){this.qualification=qualification;this.actor=actor;this.action=action;this.fromStatus=fromStatus;this.toStatus=toStatus;this.reason=reason;this.snapshotJson=snapshotJson;}
    @PrePersist void insert(){if(id==null)id=UUID.randomUUID();if(createdAt==null)createdAt=Instant.now();}
    public UUID getId(){return id;} public UUID getQualificationId(){return qualification.getId();} public UUID getActorId(){return actor.getId();} public String getActorName(){return actor.getFullName();} public String getAction(){return action;} public String getFromStatus(){return fromStatus;} public String getToStatus(){return toStatus;} public String getReason(){return reason;} public String getSnapshotJson(){return snapshotJson;} public Instant getCreatedAt(){return createdAt;}
}
