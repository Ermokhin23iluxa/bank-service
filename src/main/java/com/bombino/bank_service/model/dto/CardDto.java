package com.bombino.bank_service.model.dto;

import com.bombino.bank_service.model.enums.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CardDto(
        UUID id,
        UUID userId,
        BigDecimal balance,
        String maskedPan,
        LocalDate expireDate,
        CardStatus status,
        String currency
) {
}
