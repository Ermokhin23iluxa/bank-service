package com.bombino.bank_service.component;

import com.bombino.bank_service.model.entity.OutboxEvent;
import com.bombino.bank_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxLockService {
    private final OutboxEventRepository outboxEventRepository;
    private final String instanceId = "instance-" + UUID.randomUUID().toString().substring(0, 8);

    @Transactional
    public List<OutboxEvent> acquireEvents(int batchSize) {
        int unlocked = unlockStaleLocks();
        if (unlocked > 0) {
            log.info("Автоматически разблокировано {} зависших событий", unlocked);
        }
        List<OutboxEvent> pending = outboxEventRepository.findTop100ByUnlockedOrderByCreatedAtAsc();
        if (pending.isEmpty()) return List.of();

        List<OutboxEvent> toProcess = pending.stream()
                .limit(batchSize)
                .toList();
        List<UUID> ids = toProcess.stream()
                .map(OutboxEvent::getId)
                .toList();
        int lockedCount = outboxEventRepository.lockEvents(ids, instanceId, OffsetDateTime.now());
        log.debug("Заблокировано {}/{} событий", lockedCount, toProcess.size());
        return toProcess.stream()
                .limit(lockedCount)
                .toList();
    }

    @Transactional
    public boolean markAsProcessed(OutboxEvent event) {
        int updated = outboxEventRepository.markAsProcessed(event.getId(), instanceId);
        return updated > 0;
    }

    @Transactional
    public void unlockEvent(OutboxEvent event) {
        event.setLocked(false);
        event.setLockedBy(null);
        outboxEventRepository.save(event);
    }


    public int unlockStaleLocks() {
        OffsetDateTime threshold = OffsetDateTime.now().minusMinutes(5);
        return outboxEventRepository.unlockStaleLocks(threshold);
    }
}
