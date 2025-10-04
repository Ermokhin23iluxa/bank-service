package com.bombino.bank_service.advice;

import com.bombino.bank_service.exception.CardNotFoundException;
import com.bombino.bank_service.exception.IdempotentException;
import com.bombino.bank_service.exception.InsufficientFundsException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(CardNotFoundException ex) {
        log.warn("Card not found: {}", ex.getMessage());
        ApiError err = new ApiError(
                HttpStatus.NOT_FOUND.value(),
                "CARD_NOT_FOUND",
                ex.getMessage(),
                Map.of(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }
    @ExceptionHandler(IdempotentException.class)
    public ResponseEntity<ApiError> handleIdempotent(IdempotentException ex) {
        log.info("Idempotent request: {}", ex.getMessage());
        ApiError err = new ApiError(
                HttpStatus.OK.value(),
                "IDEMPOTENT",
                ex.getMessage(),
                Map.of("note","Request has been processed previously"),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiError> handleValidationException(InsufficientFundsException ex) {
        log.warn("Insufficient funds: {}", ex.getMessage());
        ApiError err = new ApiError(
                HttpStatus.CONFLICT.value(),//409
                "INSUFFICIENT_FUNDS",
                ex.getMessage(),
                Map.of(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        ApiError err = new ApiError(
                HttpStatus.CONFLICT.value(),
                "DATA_INTEGRITY_VIOLATION",
                "Несоответствие данных или уникальное ограничение",
                Map.of("error", Optional.ofNullable(ex.getRootCause()).map(Throwable::getMessage).orElse(ex.getMessage())),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> a + "; " + b));
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Некорректные данные запроса",
                Map.of("fields", fieldErrors),
                OffsetDateTime.now()
        );
        return ResponseEntity.badRequest().body(err);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleBadRequest(HttpMessageNotReadableException ex) {
        ApiError err = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "MALFORMED_JSON",
                "Не удалось разобрать тело запроса",
                Map.of("error", ex.getMessage()),
                OffsetDateTime.now()
        );
        return ResponseEntity.badRequest().body(err);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest req) {
        log.error("Unhandled exception", ex);
        ApiError err = new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "Внутренняя ошибка сервера",
                Map.of("error", ex.getMessage()),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}
