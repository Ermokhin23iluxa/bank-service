package com.bombino.bank_service.controller;

import com.bombino.bank_service.model.dto.TopupRequest;
import com.bombino.bank_service.model.dto.TransactionDto;

import com.bombino.bank_service.model.entity.Transaction;
import com.bombino.bank_service.model.mapper.TransactionMapper;
import com.bombino.bank_service.service.CardTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cards")
@Tag(name = "Transaction Management", description = "API для управления операциями с картами")
public class TransactionController {

    private final CardTransactionService cardTransactionService;
    private final TransactionMapper mapper;

    @Operation(
            summary = "Пополнить баланс карты",
            description = "Пополнение баланса карты по id, amount, idempotency-key"
    )
    @PostMapping("/{id}/topup")
    public ResponseEntity<TransactionDto> topUp(
            @Parameter(description = "UUID карты", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable("id") UUID id,
            @Parameter(description = "Сумма пополнения", example = "500")
            @RequestBody TopupRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        Transaction transaction = cardTransactionService.topUp(id, request.getAmount(), idempotencyKey);
        return ResponseEntity.ok(mapper.toDto(transaction));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<TransactionDto> withdraw(
            @PathVariable("id") UUID id,
            @RequestBody TopupRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        Transaction transaction = cardTransactionService.withdraw(id, request.getAmount(), idempotencyKey);
        return ResponseEntity.ok(mapper.toDto(transaction));
    }
}
