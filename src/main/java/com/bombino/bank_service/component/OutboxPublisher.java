package com.bombino.bank_service.component;

import com.bombino.bank_service.kafka.MessageSender;
import com.bombino.bank_service.model.entity.OutboxEvent;
import com.bombino.bank_service.repository.OutboxEventRepository;
import com.bombino.bank_service.service.OutboxTxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxPublisher {
    private final OutboxEventRepository outboxEventRepository;
    private final MessageSender messageSender;
    private final OutboxTxService txService;

    @Scheduled(fixedDelayString = "${outbox.poll.ms:15000}") // проверка неотправленных событий каждые 5 сек
    public void publishPending() {
        //поиск не обработанных(неотправленных) событий
        List<OutboxEvent> pending = outboxEventRepository.findTop100ByProcessedFalseOrderByCreatedAtAsc();
        log.info("Найдено {} ожидающих outbox событий", pending.size());

        for (OutboxEvent ev : pending) {
            try {
                String topic = "card-events"; // или: ev.getEventType()
                messageSender.send(topic, ev.getPayload(),ev.getId(),ev.getAggregateId());

                //int updated = outboxEventRepository.markProcessed(ev.getId()); // идемпотентность
                //int updated = markProcessedInNewTx(ev.getId());
                int updated = txService.markProcessedInNewTx(ev.getId());
                if (updated > 0) {
                    log.info("Отметил outbox {} как обработанный", ev.getId());
                } else {
                    log.info("Outbox {} уже был обработан другим воркером", ev.getId());
                }

            } catch (Exception exception) {
                log.warn("Ошибка публикации outbox с id={}, попробуйте позже", ev.getId(), exception);
            }
        }
    }
}
