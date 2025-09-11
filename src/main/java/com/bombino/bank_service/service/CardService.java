package com.bombino.bank_service.service;

import com.bombino.bank_service.exception.CardNotFoundException;
import com.bombino.bank_service.model.dto.CardDto;
import com.bombino.bank_service.model.entity.Card;
import com.bombino.bank_service.model.mapper.CardMapper;
import com.bombino.bank_service.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

    public void deleteCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                ()->new CardNotFoundException("Карты с id: {" +id+"} не существует" ));
        cardRepository.delete(card);
    }

    public CardDto getCardById(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                ()->new CardNotFoundException("Карты с id: {" +id+"} не существует" ));
        return mapper.toDto(card);
    }

    public List<CardDto> getAllCards() {
        List<Card> cards = cardRepository.findAll();
        return cards.stream().map(mapper::toDto).toList();
    }
}
