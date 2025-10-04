package com.bombino.bank_service.model.entity;

import com.bombino.bank_service.model.enums.OperationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transactions")
@Getter
@Setter
@Schema(description = "Модель транзакции")
public class Transaction {
    @Id
    @Column(columnDefinition = "uuid")
    @Schema(description = "ID транзакции")
    private UUID id;

    @Column(columnDefinition = "uuid")
    @Schema(description = "ID карты")
    private UUID cardId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @Schema(description = "Тип операции", example = "CREDIT")
    private OperationType type;

    @Column(nullable = false, precision = 18, scale = 2)
    @Schema(description = "Сумма транзакции", example = "100.00")
    private BigDecimal amount;

    @Column(name = "idempotency_key", unique = true)
    @Schema(description = "Ключ идемпотентности")
    private String idempotencyKey;

    @Column(name = "created_at")
    @Schema(description = "Дата транзакции")
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = OffsetDateTime.now();

    }
}
