package com.bombino.bank_service.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnsuccessfulTransferService {

    private final TransferRepository transferRepository;
    @Transactional
    public void markTransferFailedAsync(UUID transferId, String reason) {
        Transfer t = transferRepository.findById(transferId).orElse(null);
        if (t != null) {
            t.setStatus("FAILED");
            transferRepository.save(t);
        }
    }
}
