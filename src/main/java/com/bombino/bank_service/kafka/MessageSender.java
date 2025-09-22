package com.bombino.bank_service.kafka;

import java.util.UUID;

public interface MessageSender {
    void send(String topic, String payload) throws Exception;
    void send(String topic, String payload, UUID eventId, UUID aggregateId) throws Exception;
}
