package com.bombino.bank_service.component;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbInit {
    private final JdbcTemplate jdbc;
    public void createIndexesIfNotExist(){
        // индекс для idempotency_key (позволяет null)
        jdbc.execute(
                "CREATE UNIQUE INDEX IF NOT EXISTS uq_transactions_idempotency " +
                        "ON transactions (idempotency_key) WHERE idempotency_key IS NOT NULL;"
        );
        // индекс для pan_hash
        jdbc.execute(
                "CREATE UNIQUE INDEX IF NOT EXISTS uq_cards_pan_hash ON cards (pan_hash);"
        );
    }
}
