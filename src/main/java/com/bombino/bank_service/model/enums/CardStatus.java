package com.bombino.bank_service.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус карты")
public enum CardStatus {
    @Schema(description = "Карта активна и используется")               ACTIVE,
    @Schema(description = "Карта заблокирована (временная блокировка)") STOPPED,
    @Schema(description = "Карта закрыта (постоянная блокировка)")      CLOSED,
    @Schema(description = "Ожидание активации или обработки")           PENDING
}
