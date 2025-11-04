package com.bombino.bank_service.deadLetter;

import com.bombino.bank_service.model.entity.OutboxEvent;
import com.bombino.bank_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeadLetterService {
    private final DeadLetterRepository deadLetterRepository;
    private final OutboxEventRepository outboxEventRepository;

    public DeadLetter save(UUID eventId,String topic, String payload,String error){
        DeadLetter deadLetter = DeadLetter.builder()
                .attempts(0)
                .eventId(eventId)
                .payload(payload)
                .topic(topic)
                .createdAt(OffsetDateTime.now())
                .error(error)
                .build();
        return deadLetterRepository.save(deadLetter);
    }
    public Optional<DeadLetter> findById(UUID id) {
        return deadLetterRepository.findById(id);
    }
    public List<DeadLetter> findAll() {
        return deadLetterRepository.findAll();
    }
    public void delete(UUID id) {
        deadLetterRepository.deleteById(id);
    }

    @Transactional
    public OutboxEvent requeueToOutbox(UUID deadLetterId) {
        DeadLetter dl = deadLetterRepository.findById(deadLetterId)
                .orElseThrow(() -> new IllegalArgumentException("DeadLetter не найден: " + deadLetterId));

        OutboxEvent ev = new OutboxEvent();

        ev.setAggregateType("DEADLETTER_REPLAY");
        ev.setAggregateId(dl.getEventId());
        ev.setEventType(dl.getTopic() == null ? "deadLetter" : dl.getTopic());
        ev.setPayload(dl.getPayload());

        outboxEventRepository.save(ev);


        deadLetterRepository.deleteById(deadLetterId);
        return ev;
    }
}
