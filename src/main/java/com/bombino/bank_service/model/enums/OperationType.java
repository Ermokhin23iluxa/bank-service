package com.bombino.bank_service.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Тип операции")
public enum OperationType {
    @Schema(description = "Пополнение") CREDIT,
    @Schema(description = "Списание")   DEBIT
}
