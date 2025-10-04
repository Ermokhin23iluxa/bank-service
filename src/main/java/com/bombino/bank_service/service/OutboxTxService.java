package com.bombino.bank_service.service;

import com.bombino.bank_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class OutboxTxService {
    private final OutboxEventRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int markProcessedInNewTx(UUID outboxId) {
        return repository.markProcessed(outboxId);
    }
}
