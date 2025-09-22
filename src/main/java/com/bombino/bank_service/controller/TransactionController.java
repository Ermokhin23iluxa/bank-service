package com.bombino.bank_service.controller;

import com.bombino.bank_service.model.dto.TopupRequest;
import com.bombino.bank_service.model.dto.TransactionDto;

import com.bombino.bank_service.model.entity.Transaction;
import com.bombino.bank_service.model.mapper.TransactionMapper;
import com.bombino.bank_service.service.CardTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cards")
public class TransactionController {

    private final CardTransactionService cardTransactionService;
    private final TransactionMapper mapper;

    @PostMapping("/id/topup")
    public ResponseEntity<TransactionDto> topUp(
            @PathVariable("id") UUID id,
            @RequestBody TopupRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        Transaction transaction = cardTransactionService.topUp(id, request.getAmount(), idempotencyKey);
        return ResponseEntity.ok(mapper.toDto(transaction));
    }

    @PostMapping("/id/withdraw")
    public ResponseEntity<TransactionDto> withdraw(
            @PathVariable("id") UUID id,
            @RequestBody TopupRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        Transaction transaction = cardTransactionService.withdraw(id, request.getAmount(), idempotencyKey);
        return ResponseEntity.ok(mapper.toDto(transaction));
    }
}
