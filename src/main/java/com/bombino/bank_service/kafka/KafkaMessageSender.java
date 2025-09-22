package com.bombino.bank_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaMessageSender implements MessageSender {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final int MAX_ATTEMPTS = 5;

    @Retryable(
            retryFor = {TimeoutException.class, KafkaException.class},
            maxAttempts = MAX_ATTEMPTS,
            backoff = @Backoff(delay = 1000)
    )
    @Override
    public void send(String topic, String payload) throws Exception {
        try {
            kafkaTemplate.send(topic, payload).get(5, TimeUnit.SECONDS);
            log.info("Кафка успешно отправила в топик={} payloadLen={}", topic, payload == null ? 0 : payload.length());
        } catch (Exception exception) {
            log.error("Ошибка отправки в топик Kafka={}", topic, exception);
            throw new RuntimeException("Ошибка отправки сообщения в Kafka", exception);
        }
    }

    @Override
    public void send(String topic, String payload, UUID eventId, UUID aggregateId) throws Exception {

    }

    @Recover
    public void sendFallback(Exception ex, String topic, String payload) {
        log.error("Все {} попыток отправки в топик {} провалились", MAX_ATTEMPTS, topic, ex);

    }
}
