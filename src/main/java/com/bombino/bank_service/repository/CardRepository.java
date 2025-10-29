package com.bombino.bank_service.repository;

import com.bombino.bank_service.model.entity.Card;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {
    Boolean existsByPanHash(String panHash);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Card c where c.id= :id")
    Optional<Card> findByIdForUpdate(@Param("id") UUID id);

    List<Card> findByUserId(@Param("userId") UUID userId);
}
