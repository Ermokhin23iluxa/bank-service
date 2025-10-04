package com.bombino.bank_service.model.dto;

import com.bombino.bank_service.model.enums.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Schema(description = "DTO модель банковской карты")
public record CardDto(
        @Schema(description = "Уникальный идентификатор карты", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Уникальный идентификатор пользователя", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID userId,
        @Schema(description = "Баланс карты", example = "1500.50")
        BigDecimal balance,
        @Schema(description = "Замаскированный номер карты", example = "123456****1234")
        String maskedPan,
        @Schema(description = "Дата окончания действия карты", example = "2028-01-15T10:30:00")
        LocalDate expireDate,
        @Schema(description = "Статус карты", example = "ACTIVE")
        CardStatus status,
        @Schema(description = "Денежная валюта", example = "RUB")
        String currency
) {
}
