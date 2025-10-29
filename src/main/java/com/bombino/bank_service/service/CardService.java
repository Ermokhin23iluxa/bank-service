package com.bombino.bank_service.service;

import com.bombino.bank_service.model.dto.CardDto;
import com.bombino.bank_service.model.entity.Card;

import java.util.List;
import java.util.UUID;


public interface CardService {
    CardDto createCard(UUID userid);

    void deleteCard(UUID id);

    CardDto getCardById(UUID id);

    List<CardDto> getAllCards();

    List<CardDto> getCardsByUserId(UUID id);
}
