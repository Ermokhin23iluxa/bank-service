package com.bombino.bank_service.transfer;

import java.util.UUID;

public record TransferResult(
        UUID transferId,
        UUID debitTransactionId,
        UUID creditTransactionId
) {}
