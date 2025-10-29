package com.bombino.bank_service.transfer;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_transfers_idempotency_key",
                columnNames = {"idempotency_key"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "idempotency_key", unique = true,nullable = true)
    private String idempotencyKey;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(length = 3, nullable = false)
    private String currency = "RUB";

    @Column(name = "from_card_id", columnDefinition = "uuid", nullable = false)
    private UUID fromCardId;

    @Column(name = "to_card_id", columnDefinition = "uuid", nullable = false)
    private UUID toCardId;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, COMPLETED, FAILED

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = OffsetDateTime.now();
    }
}

