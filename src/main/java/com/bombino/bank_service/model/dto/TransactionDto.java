package com.bombino.bank_service.model.dto;

import com.bombino.bank_service.model.enums.OperationType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
@Schema(description = "DTO модель банковской карты")
public record TransactionDto(
        @Schema(description = "Уникальный id транзакции")
        UUID id,
        @Schema(description = "Уникальный id карты")
        UUID cardId,
        @Schema(description = "Тип операции")
        OperationType type,
        @Schema(description = "Сумма операции")
        BigDecimal amount,
        @Schema(description = "Дата операции")
        OffsetDateTime createdAt
) {
}
