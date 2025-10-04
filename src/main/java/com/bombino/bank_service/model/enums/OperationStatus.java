package com.bombino.bank_service.model.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статус операции")
public enum OperationStatus {
    @Schema(description = "операция создана и ожидает обработки")                       PENDING,
    @Schema(description = "операция в процессе исполнения")                             PROCESSING,
    @Schema(description = "операция успешно завершена")                                 COMPLETED,
    @Schema(description = "операция неуспешна, произошла ошибка")                       FAILED,
    @Schema(description = "операция отменена пользователем или системой")               CANCELLED,
    @Schema(description = "операция отменена после выполнения (реверс)")                REVERSED,
    @Schema(description = "операция отклонена (например, из-за недостатка средств)")    DECLINED,
    @Schema(description = "операция просрочена и не выполнена")                         EXPIRED
}
