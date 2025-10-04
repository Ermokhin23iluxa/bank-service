package com.bombino.bank_service.model.mapper;

import com.bombino.bank_service.model.dto.TransactionDto;
import com.bombino.bank_service.model.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper extends Mappable<Transaction, TransactionDto> {
}
