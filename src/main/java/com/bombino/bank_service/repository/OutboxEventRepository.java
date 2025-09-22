package com.bombino.bank_service.repository;

import com.bombino.bank_service.model.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findTop100ByProcessedFalseOrderByCreatedAtAsc();

    //делает update/метод возвращает число затронутых строк
    @Modifying
    @Query("update OutboxEvent e set e.processed = true where e.id=:id and e.processed = false")
    int markProcessed(@Param("id") UUID outboxId);
}
