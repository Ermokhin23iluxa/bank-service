package com.bombino.bank_service.model.mapper;

import com.bombino.bank_service.model.dto.CardDto;
import com.bombino.bank_service.model.entity.Card;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardMapper extends Mappable<Card, CardDto> {
}
