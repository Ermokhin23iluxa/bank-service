package com.bombino.bank_service.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table(name = "outbox")
@Entity
@Setter
@Getter
public class OutboxEvent {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String aggregateType;

    @Column(columnDefinition = "uuid")
    private UUID aggregateId;

    private String eventType;

    @Column(columnDefinition = "jsonb")
    private String payload;

    private boolean processed = false;

    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = OffsetDateTime.now();
    }
}

