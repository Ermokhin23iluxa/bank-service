package com.bombino.bank_service.model.mapper;

import com.bombino.bank_service.model.dto.TransactionDto;
import com.bombino.bank_service.model.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper extends Mappable<Transaction, TransactionDto> {
}
