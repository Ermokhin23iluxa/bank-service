package com.bombino.bank_service.transfer;

import com.bombino.bank_service.exception.CardNotFoundException;
import com.bombino.bank_service.exception.InsufficientFundsException;
import com.bombino.bank_service.model.entity.Card;
import com.bombino.bank_service.model.entity.OutboxEvent;
import com.bombino.bank_service.model.entity.Transaction;
import com.bombino.bank_service.model.enums.OperationType;
import com.bombino.bank_service.repository.CardRepository;
import com.bombino.bank_service.repository.OutboxEventRepository;
import com.bombino.bank_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {
    private final TransferRepository transferRepository;
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final UnsuccessfulTransferService unsuccessfulTransferService;


    @Transactional
    public TransferResult transfer(UUID fromCardId,
                                   UUID toCardId,
                                   BigDecimal amount,
                                   String idempotencyKey) {
        log.info("Начало перевода с {} на {} сумма={} idempotencyKey={}", fromCardId, toCardId, amount, idempotencyKey);

        if (fromCardId.equals(toCardId)) {
            throw new IllegalArgumentException("fromCardId должен отличаться от toCardId");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("сумма должна быть > 0");
        }
        if (idempotencyKey != null) {
            Optional<Transfer> existingTransfer = transferRepository.findByIdempotencyKey(idempotencyKey);
            if (existingTransfer.isPresent()) {
                UUID tId = existingTransfer.get().getId();
                // выбираем связанные транзакции по transferId
                List<Transaction> txs = transactionRepository.findByTransferId(tId);
                UUID debitId = txs.stream()
                        .filter(t -> t.getType() == OperationType.DEBIT)
                        .findFirst()
                        .map(Transaction::getId)
                        .orElse(null);
                UUID creditId = txs.stream()
                        .filter(t -> t.getType() == OperationType.CREDIT)
                        .findFirst()
                        .map(Transaction::getId)
                        .orElse(null);
                log.info("Обнаружена идемпотентная передача, возвращающая существующую передачу {}", tId);
                return new TransferResult(tId, debitId, creditId);
            }
        }

        Transfer transfer = Transfer.builder()
                .idempotencyKey(idempotencyKey)
                .amount(amount)
                .currency("RUB")
                .fromCardId(fromCardId)
                .toCardId(toCardId)
                .status("PENDING")
                .build();

        try {
            transfer = transferRepository.saveAndFlush(transfer);
        } catch (DataIntegrityViolationException ex) {
            // одновременная попытка создала передачу с тем же ключом idempotencyKey — выборка и возврат
            if (idempotencyKey != null) {
                Transfer existing = transferRepository.findByIdempotencyKey(idempotencyKey)
                        .orElseThrow(() -> new RuntimeException("Параллельная передача создана, но не найдена", ex));
                List<Transaction> txs = transactionRepository.findByTransferId(existing.getId());
                UUID debitId = txs.stream().filter(t -> t.getType() == OperationType.DEBIT).findFirst().map(Transaction::getId).orElse(null);
                UUID creditId = txs.stream().filter(t -> t.getType() == OperationType.CREDIT).findFirst().map(Transaction::getId).orElse(null);
                return new TransferResult(existing.getId(), debitId, creditId);
            } else {
                throw ex;
            }
        }
        UUID transferId = transfer.getId();

        // 3) Блокируем карты в детерминированном порядке
        UUID firstLock = fromCardId.compareTo(toCardId) < 0 ? fromCardId : toCardId;
        UUID secondLock = firstLock.equals(fromCardId) ? toCardId : fromCardId;

        Card firstCard = cardRepository.findByIdForUpdate(firstLock)
                .orElseThrow(() -> new CardNotFoundException(firstLock));
        Card secondCard = cardRepository.findByIdForUpdate(secondLock)
                .orElseThrow(() -> new CardNotFoundException(secondLock));

        // сопостовляем заблокированные карты с пременными
        Card fromCard = firstCard.getId().equals(fromCardId) ? firstCard : secondCard;
        Card toCard = fromCard == firstCard ? secondCard : firstCard;

        if (fromCard.getBalance().compareTo(amount) < 0) {
            unsuccessfulTransferService.markTransferFailedAsync(transferId, "INSUFFICIENT_FUNDS");
            throw new InsufficientFundsException();
        }

        // изменяем баланс
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));
        cardRepository.save(fromCard);
        cardRepository.save(toCard);


        // Создаем записи в журнал транзакций, где обе будут ссылаться на transferId
        Transaction debitTx = Transaction.builder()
                .cardId(fromCard.getId())
                .type(OperationType.DEBIT)
                .amount(amount)
                .currency(transfer.getCurrency())
                .transferId(transferId)
                .build();
        transactionRepository.save(debitTx);

        Transaction creditTx = Transaction.builder()
                .cardId(toCard.getId())
                .type(OperationType.CREDIT)
                .amount(amount)
                .currency(transfer.getCurrency())
                .transferId(transferId)
                .build();
        transactionRepository.save(creditTx);

        // 7) Один евент для перевода
        OutboxEvent evt = new OutboxEvent();
        evt.setAggregateType("TRANSFER");
        evt.setAggregateId(transferId);
        evt.setEventType("TransferCompleted");
        evt.setPayload(String.format("{\"transferId\":\"%s\",\"debitId\":\"%s\",\"creditId\":\"%s\",\"amount\":%s}",
                transferId, debitTx.getId(), creditTx.getId(), amount));
        outboxEventRepository.save(evt);

        // 8) Mark transfer completed (optional) — still inside tx
        transfer.setStatus("COMPLETED");
        transfer.setCompletedAt(OffsetDateTime.now());
        transferRepository.save(transfer);

        log.info("Перевод успешен transferId={}, debit={}, credit={}", transferId, debitTx.getId(), creditTx.getId());
        return new TransferResult(transferId, debitTx.getId(), creditTx.getId());
    }
}
