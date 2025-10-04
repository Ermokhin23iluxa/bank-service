package com.bombino.bank_service.advice;

import java.time.OffsetDateTime;
import java.util.Map;

public record ApiError(
        int status,
        String code,
        String message,
        Map<String, Object> details,
        OffsetDateTime timestamp
) {
}
