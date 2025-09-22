package com.bombino.bank_service.exception;

import com.bombino.bank_service.model.entity.Transaction;
import lombok.Getter;

@Getter
public class IdempotentException extends RuntimeException {
    private final Transaction transaction;

    public IdempotentException(Transaction transaction) {
        super("Idempotent request: transaction " + transaction.getId());
        this.transaction = transaction;
    }
}
