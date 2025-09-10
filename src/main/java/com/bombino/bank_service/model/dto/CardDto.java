package com.bombino.bank_service.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardDto(
        Long id,
        BigDecimal money,
        String cardNumber,
        LocalDate expirationDate,
        Integer cvv
) {
}
