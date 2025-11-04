package com.bombino.bank_service.component;

import com.bombino.bank_service.kafka.MessageSender;
import com.bombino.bank_service.model.entity.OutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisherWithLock {
    private final MessageSender messageSender;
    private final OutboxLockService lockService;


    @Scheduled(fixedRateString = "${outbox.poll.ms:15000}", initialDelay = 30000)
    public void publishPending() {
        List<OutboxEvent> lockedEvents = lockService.acquireEvents(100);
        if (lockedEvents.isEmpty()) {
            log.debug("Нет событий для обработки");
            return;
        }
        log.info("Найдено {} заблокированных outbox событий", lockedEvents.size());

        for (OutboxEvent ev : lockedEvents) {
            try {
                String topic = mapEventTypeToTopic(ev.getEventType());
                messageSender.send(topic, ev.getPayload(), ev.getId(), ev.getAggregateId());

                boolean success = lockService.markAsProcessed(ev);
                if (success) {
                    log.debug("Отметил outbox {} как обработанный", ev.getId());
                } else {
                    log.info("Outbox {} уже был обработан другим воркером", ev.getId());
                }

            } catch (Exception exception) {
                lockService.unlockEvent(ev);
                log.warn("Ошибка публикации outbox с id={}, разблокировано для повторной попытки", ev.getId(), exception);
            }
        }
    }

    private String mapEventTypeToTopic(String eventType) {
        if (eventType == null) return "card-events";
        return switch (eventType.toLowerCase()) {
            case "cardcredited", "carddebited","transfercompleted" -> "transaction-events";
            case "card_created", "card_blocked" -> "card-events";
            case "deadletter" -> "dead-letter";
            case "account_created", "balance_updated" -> "account-events";
            default -> "general-events";
        };
    }

    @Scheduled(fixedRate = 300000)
    public void cleanupStaleLocks() {
        try {
            int unlocked = lockService.unlockStaleLocks();
            if (unlocked > 0) {
                log.info("Автоматически разблокировано {} зависших событий", unlocked);
            }
        } catch (Exception ex) {
            log.error("Ошибка при очистке блокировок", ex);
        }
    }
}
