package com.bombino.bank_service.service;

import com.bombino.bank_service.exception.CardNotFoundException;
import com.bombino.bank_service.exception.IdempotentException;
import com.bombino.bank_service.exception.InsufficientFundsException;
import com.bombino.bank_service.model.entity.Card;
import com.bombino.bank_service.model.entity.OutboxEvent;
import com.bombino.bank_service.model.entity.Transaction;
import com.bombino.bank_service.model.enums.OperationType;
import com.bombino.bank_service.repository.CardRepository;
import com.bombino.bank_service.repository.OutboxEventRepository;
import com.bombino.bank_service.repository.TransactionRepository;
import com.bombino.bank_service.transfer.TransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardTransactionService {
    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final TransferRepository transferRepository;

    @Transactional
    public Transaction topUp(UUID cardId, BigDecimal amount, String idempotencyKey) {
        log.info("Start method topUp in CardTransactionService");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("amount>0");

        if (idempotencyKey != null) {
            Optional<Transaction> existing = transactionRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                log.info("Idempotent topUp detected, returning existing tx {}", existing.get().getId());
                return existing.get();
            }
        }
        try {
            Card card = cardRepository.findByIdForUpdate(cardId).orElseThrow(
                    () -> new CardNotFoundException(cardId)
            );
            card.setBalance(card.getBalance().add(amount));
            cardRepository.save(card);

            Transaction tx = new Transaction();
            tx.setCardId(card.getId());
            tx.setType(OperationType.CREDIT);
            tx.setAmount(amount);
            tx.setIdempotencyKey(idempotencyKey);
            transactionRepository.save(tx);

            OutboxEvent evt = new OutboxEvent();
            evt.setAggregateType("CARD");
            evt.setAggregateId(card.getId());
            evt.setEventType("CardCredited");
            evt.setPayload("{\"transactionId\":\"" + tx.getId() + "\",\"amount\":" + amount + "}");
            outboxEventRepository.save(evt);

            log.info("TopUp succeeded: cardId={}, txId={}", cardId, tx.getId());
            return tx;
        } catch (DataIntegrityViolationException ex) {
            if (idempotencyKey != null) {
                log.warn("DataIntegrityViolationException for idempotencyKey={}, fetching existing transaction", idempotencyKey);
                return transactionRepository.findByIdempotencyKey(idempotencyKey)
                        .orElseThrow(() ->
                                new RuntimeException("Idempotent tx not found after constraint violation", ex));
            }
            throw ex;
        }
    }

    @Transactional
    public Transaction withdraw(UUID cardId, BigDecimal amount, String idempotencyKey) {
        log.info("Start method withdraw in CardTransactionService");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("amount>0");

        if (idempotencyKey != null) {
            transactionRepository.findByIdempotencyKey(idempotencyKey).ifPresent(tx -> {
                throw new IdempotentException(tx);
            });
        }

        try {
            Card card = cardRepository.findByIdForUpdate(cardId)
                    .orElseThrow(() -> new CardNotFoundException(cardId));
            if (card.getBalance().compareTo(amount) < 0) throw new InsufficientFundsException();

            card.setBalance(card.getBalance().subtract(amount));
            cardRepository.save(card);

            Transaction tx = new Transaction();
            tx.setCardId(card.getId());
            tx.setType(OperationType.DEBIT);
            tx.setAmount(amount);
            tx.setIdempotencyKey(idempotencyKey);
            transactionRepository.save(tx);

            OutboxEvent evt = new OutboxEvent();
            evt.setAggregateType("CARD");
            evt.setAggregateId(card.getId());
            evt.setEventType("CardDebited");
            evt.setPayload("{\"transactionId\":\"" + tx.getId() + "\",\"amount\":" + amount + "}");
            outboxEventRepository.save(evt);
            log.info("Списание успешно: cardId={}, txId={}", cardId, tx.getId());
            return tx;
        } catch (DataIntegrityViolationException ex) {
            if (idempotencyKey != null) {
                log.warn("DataIntegrityViolationException for idempotencyKey={}, fetching existing transaction", idempotencyKey);
                return transactionRepository.findByIdempotencyKey(idempotencyKey)
                        .orElseThrow(() ->
                                new RuntimeException("Idempotent tx not found after constraint violation", ex));
            }
            throw ex;
        }
    }

}
