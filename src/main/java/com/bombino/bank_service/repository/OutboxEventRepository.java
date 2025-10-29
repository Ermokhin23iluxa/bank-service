package com.bombino.bank_service.repository;

import com.bombino.bank_service.model.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findTop100ByProcessedFalseOrderByCreatedAtAsc();

    //делает update/метод возвращает число затронутых строк
    @Transactional
    @Modifying
    @Query("update OutboxEvent e set e.processed = true where e.id=:id and e.processed = false")
    int markProcessed(@Param("id") UUID outboxId);


    //todo: новые методы для работы с блокировками



    @Query("""
    select e from OutboxEvent e where e.processed=false
    and e.locked=false
    order by e.createdAt asc
    """)
    List<OutboxEvent> findTop100ByUnlockedOrderByCreatedAtAsc();

    @Modifying
    @Query("""
    update OutboxEvent e set
    e.locked = true,
    e.lockedBy =:lockedBy,
    e.lockedAt=:now
    where e.id in :ids and e.locked=false
    """)
    @Transactional
    int lockEvents(@Param("ids") List<UUID> ids,
                   @Param("lockedBy") String lockedBy,
                   @Param("now")OffsetDateTime now
                   );
    @Transactional
    @Modifying
    @Query("update OutboxEvent e set e.processed = true,e.locked=false where e.id=:id and e.lockedBy =:lockedBy")
    int markAsProcessed(@Param("id") UUID outboxId,
                        @Param("lockedBy") String lockedBy
    );

    @Transactional
    @Modifying
    @Query("""
    UPDATE OutboxEvent e SET
    e.locked = false,
    e.lockedBy = null
    WHERE e.locked = true AND e.lockedAt < :threshold
    """)
    int unlockStaleLocks(@Param("threshold") OffsetDateTime threshold);

}
