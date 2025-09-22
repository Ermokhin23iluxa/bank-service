package com.bombino.bank_service.model.dto;

import com.bombino.bank_service.model.enums.OperationType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionDto(
        UUID id,
        UUID cardId,
        OperationType type,
        BigDecimal amount,
        OffsetDateTime createdAt
) {
}
