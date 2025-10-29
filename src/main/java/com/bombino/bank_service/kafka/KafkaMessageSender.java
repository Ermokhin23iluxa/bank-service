package com.bombino.bank_service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaMessageSender implements MessageSender {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final int MAX_ATTEMPTS = 5;


    @Override
    public void send(String topic, String payload) throws Exception {
        send(topic, payload, null, null);
    }


    @Retryable(
            retryFor = {TimeoutException.class, KafkaException.class, ExecutionException.class},
            maxAttempts = MAX_ATTEMPTS,
            backoff = @Backoff(delay = 1000)
    )
    @Override
    public void send(String topic, String payload, UUID eventId, UUID aggregateId) throws Exception {
        log.info("Попытка отправить сообщение в кафку");
        try{
            String key = aggregateId == null ? null : aggregateId.toString();
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, payload);
            if (eventId != null) {
                producerRecord.headers().add("eventId", eventId.toString().getBytes(StandardCharsets.UTF_8));
            }
            kafkaTemplate.send(producerRecord).get(5,TimeUnit.SECONDS);
            log.info("Кафка успешно отправила topic={} eventId={}", topic, eventId);
        }catch (ExecutionException ex){
            Throwable cause = ex.getCause();
            if(cause instanceof TimeoutException) throw (TimeoutException) cause;
            if(cause instanceof KafkaException) throw (KafkaException) cause;
            throw ex;
        }catch (InterruptedException ex){
            Thread.currentThread().interrupt();
            throw ex;
        }
    }

    @Recover
    public void sendFallback(Exception ex, String topic, String payload, UUID eventId, UUID aggregateId) {
        log.error("Все {} попытки отправить eventId {} в топик {} провалились. Ошибка: {}", MAX_ATTEMPTS,eventId, topic,ex.getMessage(), ex);
        //todo: Сохранить в dead-letter: таблицу или лог для ручной обработки
    }
}
