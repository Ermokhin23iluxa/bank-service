package com.bombino.bank_service.model.mapper;

import com.bombino.bank_service.model.dto.CardDto;
import com.bombino.bank_service.model.entity.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {
    public Card toEntity(){
        return new Card();
    }
    public CardDto toDto(Card card){
        CardDto cardDto = new CardDto(
                card.getId(),
                card.getMoney(),
                card.getCardNumber(),
                card.getExpirationDate(),
                card.getCvv()
        );
        return cardDto;
    }
}
