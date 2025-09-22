package com.bombino.bank_service.model.entity;

import com.bombino.bank_service.model.enums.CardStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "cards")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class Card {
    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @NotNull
    @Column(name = "pan_ciphertext", nullable = false, unique = true, length = 16)
    private String panCiphertext;
    @NotNull
    @Column(name = "pan_hash", nullable = false, unique = true, length = 16)
    private String panHash;

    @NotNull
    @Column(nullable = false, length = 19)
    private String maskedPan;

    @NotNull
    @FutureOrPresent
    @Column(name = "expire_date", nullable = false)
    private LocalDate expireDate;

    @NotNull
    @Column(name = "cvv_ciphertext")
    private String cvvCiphertext;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(length = 3, nullable = false)
    private String currency = "RUB";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status = CardStatus.ACTIVE;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = OffsetDateTime.now();
    }

}
