package com.bombino.bank_service.deadLetter;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_dead_letter")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeadLetter {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "event_id", columnDefinition = "uuid")
    private UUID eventId;

    private String topic;

    @Column(columnDefinition = "jsonb")
    private String payload;

    @Column(columnDefinition = "text")
    private String error;

    private Integer attempts = 0;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = OffsetDateTime.now();
        if (this.attempts == null) this.attempts = 0;
    }
}
