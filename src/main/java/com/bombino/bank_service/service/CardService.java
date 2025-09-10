package com.bombino.bank_service.service;

import com.bombino.bank_service.model.dto.CardDto;
import com.bombino.bank_service.model.entity.Card;
import com.bombino.bank_service.model.mapper.CardMapper;
import com.bombino.bank_service.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CardService {
    private final GenerationCardService generationCardService;
    private final CardRepository cardRepository;
    private final CardMapper mapper;
    private final BigDecimal startMoney = new BigDecimal(0);

    public CardDto createCard() {
        String cardNumber = generationCardService.generationCardNumber();
        LocalDate expirationDate = generationCardService.generationDate(LocalDate.now());
        Integer cvv = generationCardService.generationCVV();
        Card newCard = new Card();
        newCard.setCardNumber(cardNumber);
        newCard.setCvv(cvv);
        newCard.setExpirationDate(expirationDate);
        newCard.setMoney(startMoney);

        cardRepository.save(newCard);

        return mapper.toDto(newCard);
    }
}
