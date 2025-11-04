package com.bombino.bank_service.repository;

import com.bombino.bank_service.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    List<Transaction> findByTransferId(UUID id);

    List<Transaction> findTransactionsByCardIdIn(List<UUID> cardIds);

    List<Transaction> findTransactionsByCardId(UUID cardIds);
}
