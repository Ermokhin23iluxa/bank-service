package com.bombino.bank_service.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User,UUID> {
    Optional<User> findByNameAndPassword(String name, String password);

    Optional<Object> findByName(String name);
}
