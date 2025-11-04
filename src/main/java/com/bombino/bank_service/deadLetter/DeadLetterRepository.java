package com.bombino.bank_service.deadLetter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface DeadLetterRepository extends JpaRepository<DeadLetter, UUID> {
}
